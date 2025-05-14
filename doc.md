## Server Config 
```json5
{
  "server": {
    "fileManager": {
      "root": "<path>"
    }
  },
  "clients": [
    {
      "uid":"<uuid v4>",
      "path":"<path>",
      "port": 0 //example
    }
  ]
}
```

## Vaults
folders inside root directory are named with their unique UUID. Server maps contents of root directory into hashmap <UUID, path>. Client passed UUID of a vault that it wants to interact with.

## Server client communication

--- CLIENT ASK MODAT ---
-> client: connection
<- server: command "___ESTABLISHED_CONNECTION___" // server is ready and listening
-> client: command "___MODAT___"
-> client: [vaultUUID]
-> client: [filepath]
<- server: modat in ms

--- TRANSFER TO SERVER ---
-> client: connection
<- server: command "___ESTABLISHED_CONNECTION___" // server is ready and listening
-> client: command "___FILE___"
-> client: [vaultUUID]
-> client: [filepath]
-> client: [file modification date]
<- server: command "___OK___" # server is ready to receive
-> client: writes bytes
<- server: command "___OK___" # server received all bytes declared by client

## Files
### Vault
Root folder created upon request by server. Client can assign who has access to this vault