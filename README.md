# Checker Framework Language Server
The [language server protocol][lsp] implementation of the [Checker Framework][cf].

## Quick Start

### Visual Studio Code

1. Install the latest Checker Framework. Download [here][cf-install].

2. Install the Extension.

3. Configure VS Code settings. Available settings are:

	- `checker-framework.frameworkpath`: (mandatory) the absolute path of the Checker Framework
	- `checker-framework.tempOutputDir`: the relative path of the directory for storing temporary compiled output files, default to **build/tmp/checkerframework**
	- `checker-framework.enable`: enabling/disabling the Checker Framework without restarting VS Code, default to **true**
	- `checker-framework.checkers`: a list of checkers you would like the framework to check. More details [here](https://checkerframework.org/manual/#running)
	- `checker-framework.maxNumberOfProblems`: maximum number of issues reported per file by the framework

4. Now Checker Framework can recognizes checkers you listed and provide in-line feedback upon opening and saving a Java file.

### Steps to Build from Source

#### Visual Studio Code

1. Clone this repository

        git clone git@github.com:adamyy/checkerframework-lsp.git

2. Install dependencies and build:

        (cd bin && ./build-vscode)

3. Open **vscode-client** directory with VSCode (important! vscode needs to recognize the **current directory** as an vscode extension to run it)

4. Start debugging (F5) under the client folder, if successful, VS Code will launch an additional Extension Development Host instance that is aware of the extension.

### Contributors
- [@nadouamanda][nadouamanda] Amanda Jiang - creator, maintainer
- [@adamyy][adamyy] Adam Yang

[lsp]: <https://microsoft.github.io/language-server-protocol/>
[cf]: <https://checkerframework.org/>
[cf-install]: <https://checkerframework.org/manual/#installation>
[src]: <https://github.com/nadouamanda/checkerframework-vscode-extension>
[nadouamanda]: <https://github.com/nadouamanda>
[adamyy]: <https://github.com/adamyy>
