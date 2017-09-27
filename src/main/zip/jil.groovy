/**
 * (c) Copyright IBM Corporation 2017.
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*/

import java.util.*
import java.io.*

import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper

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

final def fileName = UUID.randomUUID().toString() + ".jil"
final def contents = props['contents']?:''

def envAutosys = processEnv['AUTOSYS']

def path
if (envAutosys == null) {
    path = "jil"
} else if (isWindows) {
    path = envAutosys + "\\bin\\jil"
} else {
    path = envAutosys + "/bin/jil"
}

println "Executable path: $path"
def pumpToStdIn = { proc ->
    BufferedOutputStream stdIn = new BufferedOutputStream(proc.getOutputStream())
    OutputStreamWriter writer = new OutputStreamWriter(stdIn)
    println "Script contents:------------"
    println "${contents}\n----------------------------"
    try {
        writer.write(contents, 0, contents.length())
    } finally {
        writer.flush()
        stdIn.close()
    }
    def out = new PrintStream(System.out, true)
    try {
        proc.waitForProcessOutput(out, out) // forward stdout and stderr to autoflushing output stream
    } finally {
        out.flush()
    }
}

try {
    if (isWindows) {
        println "Detected Windows OS"
        cmdHelper.runCommand("Executing JIL script", [path], pumpToStdIn)
    }
    else {
        println "Detected *nix OS"
        cmdHelper.runCommand("Executing JIL script", [path], pumpToStdIn)
    }
}
catch (Exception e) {
    println "Error running command: ${e.message}"
    System.exit 1
}

System.exit 0
