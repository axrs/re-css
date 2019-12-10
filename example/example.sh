#!/usr/bin/env bash
set -euo pipefail
if [ ! -d 'node_modules' ]; then
	echo "Installing node modules"
	npm install
fi

shadow-cljs () {
	lein trampoline run -m shadow.cljs.devtools.cli "$@"
}

shadow-cljs watch example
