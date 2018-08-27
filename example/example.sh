#!/usr/bin/env bash
set -euo pipefail
if [ ! -d 'node_modules/shadow-cljs' ]; then
	echo "Installing node modules"
	npm install
fi
npx shadow-cljs watch example
