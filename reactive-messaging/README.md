## TODO

### CDI edge case

1. average but OK / Calling method to for Context CI creation

2. I think OK / Thread attached lookup of message => Assumption is that is is OK, but it is?
As the context is NormalScope its activation is associated to the current thread.
Then a Producer is called where DI can be performed, hence (implicitely) with the same scope hence
the same thread.

