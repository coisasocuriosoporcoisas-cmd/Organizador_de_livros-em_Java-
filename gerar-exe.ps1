$ErrorActionPreference = "Stop"

$projeto = $PSScriptRoot
$diretorioEntrada = Join-Path $projeto "target\jpackage-input"
$diretorioSaida = Join-Path $projeto "target\dist"
$nomeJar = "catalogo-livros-1.0.0.jar"

if (-not $env:JAVA_HOME) {
    throw "Configure JAVA_HOME para um JDK 17 ou superior antes de gerar o executável."
}

$jpackage = Join-Path $env:JAVA_HOME "bin\jpackage.exe"
if (-not (Test-Path -LiteralPath $jpackage)) {
    throw "Não encontrei jpackage.exe em '$jpackage'. Instale um JDK 17 ou superior."
}

$maven = Get-Command "mvn.cmd" -ErrorAction SilentlyContinue
if ($maven) {
    $comandoMaven = $maven.Source
} else {
    $mavenTemporario = Join-Path $env:TEMP "apache-maven-3.9.11\bin\mvn.cmd"
    if (Test-Path -LiteralPath $mavenTemporario) {
        $comandoMaven = $mavenTemporario
    } else {
        throw "Maven não encontrado. Instale o Maven 3.9+ e adicione mvn.cmd ao PATH."
    }
}

$env:Path = (Join-Path $env:JAVA_HOME "bin") + ";" + $env:Path
Push-Location $projeto
try {
    & $comandoMaven --batch-mode clean package `
        "org.apache.maven.plugins:maven-dependency-plugin:3.8.1:copy-dependencies" `
        "-DincludeScope=runtime" `
        "-DoutputDirectory=$diretorioEntrada"
    if ($LASTEXITCODE -ne 0) {
        throw "A compilação ou a cópia das dependências falhou (código $LASTEXITCODE)."
    }

    Copy-Item -LiteralPath (Join-Path $projeto "target\$nomeJar") `
        -Destination $diretorioEntrada -Force
    New-Item -ItemType Directory -Force -Path $diretorioSaida | Out-Null

    & $jpackage `
        --type app-image `
        --name Estante `
        --input $diretorioEntrada `
        --main-jar $nomeJar `
        --main-class br.com.catalogo.Inicio `
        --dest $diretorioSaida `
        --java-options '--module-path=$APPDIR' `
        --java-options '--add-modules=javafx.controls'
    if ($LASTEXITCODE -ne 0) {
        throw "A geração da imagem do aplicativo falhou (código $LASTEXITCODE)."
    }

    Write-Output "Executável gerado em: $(Join-Path $diretorioSaida 'Estante\Estante.exe')"
} finally {
    Pop-Location
}
