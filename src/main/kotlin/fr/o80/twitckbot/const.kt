package fr.o80.twitckbot

const val oauthEndpoint = "https://id.twitch.tv/oauth2/authorize"
const val oauthRedirectUri = "http://localhost:%d/oauth"

const val javascriptHashRedirection = """<!doctype html>
<html>
    <body>
        <script type="text/javascript">
            let hash = window.location.hash.substr(1);
            document.location = "http://localhost:%d/capture?" + hash;
        </script>
    </body>
</html>"""
