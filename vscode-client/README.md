# Checker Framework Language Server - VS Code

## Quick Start

1. Install the latest Checker Framework. Download [here][cf-install].

2. Install the Extension.

3. Configure VS Code settings. Available settings are:

	- `checker-framework.frameworkPath`: (mandatory) the absolute path of the Checker Framework
	- `checker-framework.checkers`: a list of checkers you would like the framework to check. More details [here](https://checkerframework.org/manual/#running)
	- `checker-framework.commandLineOptions`: List of command line options that gets passed in to javac. See available options [here](https://checkerframework.org/manual/#running)

4. Now Checker Framework can recognizes checkers you listed and provide in-line feedback upon opening and saving a Java file.

### Steps to Build from Source

1. Clone this repository

        git clone git@github.com:adamyy/checkerframework-lsp.git

2. Install dependencies and build:

        (cd bin && ./build-vscode)

3. Open **vscode-client** directory with VSCode (important! vscode needs to recognize the **current directory** as an vscode extension to run it)

4. Start debugging (F5) under the client folder, if successful, VS Code will launch an additional Extension Development Host instance that is aware of the extension.

[cf-install]: <https://checkerframework.org/manual/#installation>
