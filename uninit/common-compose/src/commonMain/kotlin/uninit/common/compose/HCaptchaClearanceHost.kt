package uninit.common.compose

import androidx.compose.runtime.Composable

@Composable
fun HCaptchaClearanceHost(
    fakeHostUri: String,
    siteKey: String,
    rqData: String,
    rqToken: String,
    onCleared: (String) -> Unit,
) {
    TODO("Not implemented, this is terrifying and im scared :(")
}

private const val HTML_MIMIC = """
    <html>
        <head>
            <title>HCaptcha Clearance</title>
            <script src="https://hcaptcha.com/1/api.js?onload=onCaptchaLoad&render=explicit" async defer></script>
        </head>
        <body>
            <div id="hcaptcha" class="h-captcha" data-sitekey="SITE_KEY" data-callback="onCaptchaComplete" data-ray-data="RQ_DATA" data-ray-token="RQ_TOKEN"></div>
            <script>
                function onCaptchaComplete(token) {
                    
                }
                function onCaptchaLoad() {
                    window.hcaptcha.render('hcaptcha', {
                        sitekey: 'SITE_KEY',
                        callback: onCaptchaComplete,
                        rayData: 'RQ_DATA',
                        });
                }
            </script>
        </body>
"""