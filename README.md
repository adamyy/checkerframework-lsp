# Checker Framework Language Server
The [language server protocol][lsp] implementation of the [Checker Framework][cf].

### Editor Clients

- [ ] Visual Studio Code
- [ ] Atom

### Steps to Build from Source

> NOTE: The latest Checker Framework is required. Download [here][cf]

1. Clone this repository

    git clone git@github.com:adamyy/checkerframework-lsp.git

2. Install dependencies and build:

    cd bin && ./build-vscode

3. Open **vscode-client** directory with VSCode (important! vscode needs to recognize the **current directory** as an vscode extension to run it)

4. Debug the project (F5) and inspect

### Contributors
- [@nadouamanda][nadouamanda] Amanda Jiang - creator, maintainer
- [@adamyy][adamyy] Adam Yang

[lsp]: <https://microsoft.github.io/language-server-protocol/>
[cf]: <https://checkerframework.org/>
[src]: <https://github.com/nadouamanda/checkerframework-vscode-extension>
[nadouamanda]: <https://github.com/nadouamanda>
[adamyy]: <https://github.com/adamyy>
