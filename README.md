# Verify

## What do is do?
Verify ensures that users who are attempting to connect to the Minecraft Network are enroled and verified as students of a particular University.

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/MCUniDev/Verify/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/MCUniDev/Verify/tree/master)

## How does it work?
Verify sends an API request with the user's username and UUID to MCUni's kit service, which will then collect the user's information and check if they have
verified their account and are on the whitelist. If they are, it simply returns 'true' (ensuring that personal data is never fed back across the internet)
and Verify allows the user to play on the server. If the user is not verified and whitelisted they are disconnected with instructions on how to add their
account to the whitelist.

## How do users verify their accounts?
Users can visit mcuni.org/account to add their Minecraft username to the system. We're working on making this open-source soon.
