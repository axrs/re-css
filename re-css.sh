#!/usr/bin/env bash
set -euo pipefail
cd $(realpath $(dirname $0))
if [[ ! -f project.sh ]];then
	curl --silent -OL https://raw.githubusercontent.com/jesims/backpack/master/project.sh
fi
source project.sh
if [[ $? -ne 0 ]];then
	exit 1
fi
IFS=$'\n\t'

ensure_githooks () {
	local githooks_folder="githooks"
	if [[ "$(git config core.hooksPath)" != "$githooks_folder" ]];then
		echo "Setting up GitHooks"
		git config core.hooksPath "$githooks_folder"
	fi
}

ensure_npm_deps () {
	if [[ ! -d 'node_modules/shelljs' ]]; then
		echo "Installing node modules"
		npm install
	fi
}

shadow-cljs () {
	lein trampoline run -m shadow.cljs.devtools.cli $@
}

## deps:
## Installs Dependencies
deps () {
	echo_message 'Installing dependencies'
	npm install
	lein deps
}

## clean:
## Cleans the working project folder of generated and cached files
clean () {
	echo_message 'Cleaning'
	rm -rf resources/public/js/ .shadow-cljs/ .cpcache target/*
}

## build:
## Builds the project for release
build () {
	echo_message 'Building'
	shadow-cljs release app --source-maps
}

## test:
## args: [-r]
## Runs the unit tests
## [-r] in browser, watching for changes
test () {
	if [[ -z "${1-}" ]];then
		echo_message 'Running Tests Once'
		shadow-cljs compile test
		npx karma start --single-run
	else
		echo_message 'Watching Tests'
		shadow-cljs watch test-browser
	fi
}

## lint:
## Lints the Clojure source files
lint () {
	echo_message 'Linting'
	lein cljfmt check
}

## format:
## Formats the Clojure and markdown source files
format () {
	echo_message 'Formatting'
	lein cljfmt fix
	npx remark . \
		--use remark-preset-lint-recommended \
		--use remark-reference-links \
		--use toc \
		--use bookmarks -o
}

if ! ${CI:-false};then
	ensure_githooks
	ensure_npm_deps
fi

script-invoke $@
