## Config 
```
{
    clients: [
        {
            uid: "" /uuid v4
            root: "" /path
        }
    ]
}
```

## Server client communication

--- CLIENT ASK MODAT ---
-> client: connection
<- server: command "___ESTABLISHED_CONNECTION___" // server is ready and listening
-> client: command "___MODAT___"
-> client: [vaultId]
-> client: [filepath]
<- server: modat in ms

--- TRANSFER TO SERVER ---
-> client: connection
<- server: command "___ESTABLISHED_CONNECTION___" // server is ready and listening
-> client: command "___FILE___"
-> client: [length of file]
<- server: command "___OK___" # server is ready to receive
-> client: writes bytes
<- server: command "___OK___" # server received all bytes declared by client

## Files
### Vault
Root folder created upon request by server. Client can assign who has access to this vault