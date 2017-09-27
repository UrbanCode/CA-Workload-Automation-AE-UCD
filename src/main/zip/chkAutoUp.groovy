/**
 * (c) Copyright IBM Corporation 2017.
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*/

import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper
import java.util.*

final def workDir = new File('.').canonicalFile
final def apTool = new AirPluginTool(this.args[0], this.args[1])
final def stepProps = apTool.getStepProperties()

String osName = System.properties['os.name']
boolean isWindows = osName.toLowerCase().contains('windows')

def cmdHelper = new CommandHelper(workDir)
cmdHelper.ignoreExitValue(true)
ProcessBuilder pb = cmdHelper.getProcessBuilder()
def processEnv = pb.environment()
def asEnvNames = ["AUTOROOT", "AUTOSYS", "AUTOSERV", "AUTOUSER"]
def props = apTool.getStepProperties()
for (String name : asEnvNames) {
    String asPropValue = props[name]
    if (asPropValue != null) {
        processEnv.put(name, asPropValue)
        println "${name}: ${asPropValue}"
    }
}

def envAutosys = processEnv['AUTOSYS']

def path
if (envAutosys == null) {
    path = "sendevent"
} else if (isWindows) {
    path = envAutosys + "\\bin\\chk_auto_up"
} else {
    path = envAutosys + "/bin/chk_auto_up"
}

String failureCodesStr = stepProps['failureCodes']
def failureCodes
if (failureCodesStr == null || failureCodesStr.length() < 1) {
    failureCodes = new ArrayList<String>()
} else {
    failureCodes = Arrays.asList(failureCodesStr.split(","))
}

for (String code : failureCodes) {
    code = code.trim();
}

String successCodesStr = stepProps['successCodes']
def successCodes
if (successCodesStr == null || successCodesStr.length() < 1) {
    successCodes = new ArrayList<String>()
} else {
    successCodes = Arrays.asList(successCodesStr.split(","))
}

for (String code : successCodes) {
    code = code.trim();
}
boolean exclusive = (successCodes.size() == 0)

try {
    int cmdStatus
    println "Executable path: $path"
    if (isWindows) {
        println "Detected Windows OS"
        cmdStatus = cmdHelper.runCommand("Checking Autosys status", [path])
    }
    else {
        println "Detected *nix OS"
        cmdStatus = cmdHelper.runCommand("Checking Autosys status", [path])
    }

    if (successCodes.contains(cmdStatus.toString()) ||
            exclusive && !failureCodes.contains(cmdStatus.toString())) {
        println "Success: chk_auto_up returned exit code ${cmdStatus}"
    } else {
        println "Failure: chk_auto_up returned exit code ${cmdStatus}"
        System.exit 1
    }
}
catch (Exception e) {
    println "Error running command: ${e.message}"
    System.exit 1
}

System.exit 0
