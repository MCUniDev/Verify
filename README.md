# Verify

## What do is do?
Verify ensures that users who are attempting to connect to the Minecraft Network are enroled and verified as students of a particular University.

## How does it work?
Verify sends an API request with the user's username and UUID to MCUni's kit service, which will then collect the user's information and check if they have
verified their account and are on the whitelist. If they are, it simply returns 'true' (ensuring that personal data is never fed back across the internet)
and Verify allows the user to play on the server. If the user is not verified and whitelisted they are disconnected with instructions on how to add their
account to the whitelist.
