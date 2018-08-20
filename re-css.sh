#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

ensure_githooks () {
	local githooks_folder="githooks"
	if [ "$(git config core.hooksPath)" != "$githooks_folder" ];then
		echo "Setting up GitHooks"
		git config core.hooksPath "$githooks_folder"
	fi
}

ensure_npm_deps () {
	if [ ! -d 'node_modules/shelljs' ]; then
		echo "Installing node modules"
		npm install
	fi
}

ensure_githooks
ensure_npm_deps
lumo ./src/cli.cljs "$@"
