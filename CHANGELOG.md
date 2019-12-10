# 1.1.0

Added:

-   css live-reloading

# 1.0.0

Added:

-   CSS property support detection with `supports?`
-   the ability to change properties through transformation fns

Misc:

-   Use Bash as CLI instead of Lumo

# 0.2.1

Added:

-   Added the ability to use `defkeyframes` (and possibly other @ style css attributes)

# 0.2.0

Removed:

-   `styled` and changed to `classes`. Now only returns a map of `{:class "generated class names"}` instead of all attrs

# 0.1.1

Added:

-   Allow defining attributes as functions to be evaluated before style mount.

Changed:

-   CSS string generation to before style mount. PRs [#14][3].

# 0.1.0

Added: 

-   support for global shared styles (i.e. @font-face). PRs [#13][1]. Credits: [@AndreTheHunter][2]

# 0.0.1

Initial release

[1]: https://github.com/axrs/re-css/pull/13

[2]: https://github.com/AndreTheHunter

[3]: https://github.com/axrs/re-css/pull/14
