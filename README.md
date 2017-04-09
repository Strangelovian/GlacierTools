# GlacierTools
Get rid of Amazon Glacier data in three steps:
1. initiate vault inventory jobs. Job identifiers are stored in Json text files locally, for later use.
2. 12 hours later, fetch vault archive inventories from previously saved files. Inventory files are stored locally in
other Json files.
3. proceed to delete vault archives, using archive identifiers stored in Json files.

Nota bene:
- this is a java intellij idea console application
- you need to uncomment the Main method to perform the desired steps
- you need to setup .aws/credentials in your home directory to be able to connect to AWS
- all glacier calls are blocking, hence the archive deleting can take some time if several thousands needs to be getting rid of
