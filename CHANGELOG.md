# 1.1.0

Added:

-   CSS live-reloading. PR #25. Credits: @AndreTheHunter

Changed:

-   Updated dependencies

# 1.0.0

Added:

-   CSS property support detection with `supports?`
-   the ability to change properties through transformation fns

Misc:

-   Use Bash as CLI instead of Lumo

# 0.2.1

Added:

-   the ability to use `defkeyframes` (and possibly other @ style css attributes)

# 0.2.0

Removed:

-   `styled` and changed to `classes`. Now only returns a map of `{:class "generated class names"}` instead of all attrs

# 0.1.1

Added:

-   defining attributes as functions to be evaluated before style mount.

Changed:

-   CSS string generation to before style mount. PR #14.

# 0.1.0

Added: 

-   support for global shared styles (i.e. @font-face). PR #13. Credits: @AndreTheHunter

# 0.0.1

Initial release
