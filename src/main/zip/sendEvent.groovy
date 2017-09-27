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
    path = envAutosys + "\\bin\\sendevent"
} else {
    path = envAutosys + "/bin/sendevent"
}

def asEventValue = stepProps['asEvent']

println "Executable path: $path"

final def asParams = ["asAlarm","asAutoserv","asJobName","asApplicationName","asStatus","asUnSendEvent"]
final def asFlags = ["-A","-S","-J","-I","-s","-U"]
List<String> commandList = new ArrayList<String>()
commandList.add(path)
commandList.add("-E")
commandList.add(asEventValue)

for (String param : asParams) {
    String value = stepProps[param]
    boolean empty = false
    if (param.equals("asAlarm") || param.equals("asStatus")) {
        empty = value.equals("none")
    }
    if (value != null && value.trim().length() > 0 && !empty) {
        String flag = asFlags[asParams.indexOf(param)]
        commandList.add(flag)
        commandList.add(value)
    }
}

String additionalParams = stepProps["asAdditional"]

if (additionalParams) {
    additionalParams.split('\n').each { arg ->
        commandList.add(arg)
    }
}

try {
    if (isWindows) {
        println "Detected Windows OS"
        cmdHelper.runCommand("Sending Event to Autosys", commandList.toArray())
    }
    else {
        println "Detected *nix OS"
        cmdHelper.runCommand("Sending Event to Autosys", commandList.toArray())
    }
}
catch (Exception e) {
    println "Error running command: ${e.message}"
    System.exit 1
}

System.exit 0
