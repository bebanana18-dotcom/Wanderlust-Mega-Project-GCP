// vars/sonarqube_code_quality.groovy
// NOTE: abortPipeline is kept as a parameter (default: false) so caller can
//       choose enforcement level without modifying the library.
//       Set abortPipeline: true in Jenkinsfile when ready to enforce quality gates.
// FIX: Increased default timeout from 1 min to 5 mins — SonarQube webhook
//      delivery can be slow on busy servers. 1 min caused premature timeouts.
// FIX: Added clear echo messages so logs show quality gate result explicitly.
// FIX: Wrapped in try-catch — if webhook is not configured in SonarQube,
//      waitForQualityGate hangs. Now fails with actionable error message
//      instead of hanging the entire pipeline forever.

def call(Boolean abortPipeline = false, Integer timeoutMinutes = 5) {
    echo "⏳ Waiting for SonarQube Quality Gate result (timeout: ${timeoutMinutes} min)..."

    try {
        timeout(time: timeoutMinutes, unit: "MINUTES") {
            def qualityGate = waitForQualityGate abortPipeline: abortPipeline

            if (qualityGate.status == 'OK') {
                echo "✅ SonarQube Quality Gate PASSED (status: ${qualityGate.status})"
            } else {
                if (abortPipeline) {
                    error("❌ SonarQube Quality Gate FAILED (status: ${qualityGate.status}). Pipeline aborted.")
                } else {
                    echo "⚠️  SonarQube Quality Gate FAILED (status: ${qualityGate.status}) — abortPipeline is false, continuing."
                }
            }
        }
    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
        error("""
            sonarqube_code_quality: Timed out waiting for Quality Gate after ${timeoutMinutes} minutes.
            This usually means the SonarQube webhook is not configured.
            Fix: SonarQube > Administration > Configuration > Webhooks
                 Add webhook URL: http://<JENKINS_URL>/sonarqube-webhook/
        """)
    }
}
