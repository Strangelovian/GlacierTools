# GlacierTools
Get rid of Amazon Glacier data in three steps:
1. initiate vault inventory jobs
2. 12 hours later, fetch vault archive inventories
3. proceed to delete vault archives

Nota bene:
- you need to setup .aws/credentials in your home directory to be able to connect to AWS
- all glacier calls are blocking, hence the archive deleting can take some time if several thousands needs to be getting rid of
