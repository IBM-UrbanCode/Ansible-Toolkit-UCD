/**
 *  © Copyright IBM Corporation 2016, 2024.
 *  This is licensed under the following license.
 *  The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 *  U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
import com.urbancode.air.plugin.tool.AirPluginTool

def apTool = new AirPluginTool(this.args[0], this.args[1]) //assuming that args[0] is input props file and args[1] is output props file

def props = apTool.getStepProperties();

final def isWindows = apTool.isWindows;

def copy = props["copy"];
def src = props["src"];
def dest = props["dest"];
def creates = props["creates"];
def owner = props["owner"];
def group = props["group"];
def become = props["become"];

def now = new Date();
def time = now.getTime();
def filename="tmp"+time+".yml";

File file = new File(filename);
file.write "- hosts: localhost\n";
file << "  any_errors_fatal: true\n";
file << "  tasks:\n";

file << "  - name: unarchive\n";
file << "    unarchive:\n";
file << "      src="+src+"\n";
file << "      dest="+dest+"\n";
if (creates)
file << "      creates="+creates+"\n";
if (copy)
file << "      copy="+copy+"\n";
if (owner)
file << "      owner="+owner+"\n";
if (group)
file << "      group="+group+"\n";
if (become)
file << "    become: "+become+"\n";

def command="ansible-playbook "+filename+ " -vvvv";
println("command is "+command)
def exec =command.execute();
exec.waitFor();

def error = new StringBuffer()
exec.consumeProcessErrorStream(error)

exec.text.eachLine {println it}
println(error);
def exitValue=exec.exitValue();
if(!exitValue)
	System.exit(exitValue);
