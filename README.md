# boot-spec-coverage

```
[sparkfund/boot-spec-coverage "0.3.0"]
```

Check if your `clojure.spec` specs are actually exercised in your
test suite.

## Usage

In boot, add the following to your build file.

```
(require '[sparkfund.spec-coverage :refer :all])`
```

Then `boot spec-coverage` will trigger the coverage checker.

To opt-out of coverage per-var, attach `:spark/no-boot-spec-coverage`
metadata to your var.

## License

Copyright © 2017 SparkFund

Distributed under the Apache License, Version 2.0.
