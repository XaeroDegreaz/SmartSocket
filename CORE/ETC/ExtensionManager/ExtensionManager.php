<?php
/**
 * The ExtensionManager class allows users to choose extensions or even create new ones upon startup.
 * @author Jerome Doby
 * @package Etc
 *
 */
final class ExtensionManager {
	
	/**
	 * Basic construct
	 * @return unknown_type
	 */
	public function ExtensionManager() {
		Logger::log(__CLASS__, "ExtensionManager loaded.");
	}
	
	/**
	 * This method traverses the extensions directory and checks for valid extensions to display as a list
	 * for the user to choose from.
	 * @param $defaultExtension string "This is determined by the defaultExtension node in Config.xml"
	 * @return unknown_type
	 */
	public function Start($defaultExtension) {
		Logger::log(__CLASS__, "Listing valid extensions...");
		
		if ($handle = opendir('./Extensions')) {

			echo("*********************************************\n");
			$i = 1;
			$list = array();
			$list[] = $defaultExtension;
			while (false !== ($file = readdir($handle))) {

				if ($file != "." && $file != ".." && is_dir("./Extensions/$file")) {

					if(file_exists("./Extensions/$file/$file.php")) {
						echo("[$i]\t$file\n");
						$list[] = (string)$file;
						$i++;
					}

				}

			}
			echo("[>=".($i)."]\tCreate new extension.\n");
			closedir($handle);
			echo("*********************************************\n");
		}

		$out = fopen("php://stdout", "w");
		$in = fopen("php://stdin", "r");

		fwrite($out, "Which project should I launch? [blank = $defaultExtension]: ");
		$ProjectName = trim(fgets($in));
		echo("*=*=*=*=*=*=*=*=*=*=START*=*=*=*=*=*=*=*=*=*=\n\n");
		
		if(isset($list[(int)$ProjectName])) {
			fclose($in);
		}else {
			$this->CreateExtension();
		}
		
		$extension = new $list[(int)$ProjectName]();

	}
	
	/**
	 * Creates a new extension from this string template. I'm sure there is a better way to do it, but I was looking for something quick.
	 * @return unknown_type
	 */
	public function CreateExtension() {

		$out = fopen("php://stdout", "w");
		$in = fopen("php://stdin", "r");

		fwrite($out, "Please choose a name for your new project: ");
		$ProjectName = trim(fgets($in));

		echo("Creating project $ProjectName in './Extensions'.\n");

		@mkdir("./Extensions");
		mkdir("./Extensions/$ProjectName");
		mkdir("./Extensions/$ProjectName/Database");
		mkdir("./Extensions/$ProjectName/Flash Client");
		mkdir("./Extensions/$ProjectName/Flash Client/AS2");
		mkdir("./Extensions/$ProjectName/Flash Client/AS3");
		mkdir("./Extensions/$ProjectName/Flash Client/Build");

		$blank = fopen("./Extensions/$ProjectName/Config.xml", "wb");
		fwrite($blank,"<config></config>");
		
		$Class = fopen("./Extensions/$ProjectName/$ProjectName.php", "wb");
		$blank = fopen("./Extensions/$ProjectName/CHANGELOG.txt", "wb");
		$blank = fopen("./Extensions/$ProjectName/LICENSE.txt", "wb");
		$blank = fopen("./Extensions/$ProjectName/README.txt", "wb");

		fwrite($Class, $this->ExtensionStub($ProjectName));
		Logger::log(__CLASS__, "Extension created successfully. Preparing shutdown...");
		sleep(2);
		exit();

	}
	
	/**
	 * Writes a stub extension with all of the names and stuff filled in.
	 * @param $Extension string "Extension name"
	 * @return string
	 */
	private function ExtensionStub($Extension) {

		$Example = <<<EOM
<?php
Class $Extension implements Template{
	protected \$s;
	
	public function $Extension() {
		Logger::log(__CLASS__, "Extension loaded.");
		
		//# Let's try and locate a configuration file. This is useful for making your own config for your extension.
		if(@file_exists("Extensions/$Extension/Config.xml")) {
			\$this->Config = simplexml_load_file("Extensions/$Extension/Config.xml");
		}else {
			Logger::log(__CLASS__, "No extension config.xml file detected. Skipping...");
		}		
		
		//# Let's create a new server instance.
		\$this->s = new Server(\$this);
		
		//# Let's start the show.
		\$this->s->Start();

	}

	public function onConnect(\$socket) {
		Logger::log(__CLASS__, "The extension has received a connection.");
		
		//# Let's just send them some simple handshake data to test and see if they are able to receive.
		\$this->s->Send(\$socket, "<handshake status='1' time='".time()."' />");
	}
	
	public function onReceive(\$socket, \$raw_data) {

		Logger::log(__CLASS__, "Data Received: \$socket, ".\$raw_data);
		
		//# We can send it back to the client like this
		\$this->s->Send(\$socket, \$raw_data);
		
		//# Let's write the received data fo a file for loging purposes.
		\$file = fopen("ProtocolLog.txt", "a");
		fwrite(\$file, \$raw_data);

	}
	
	public function onDisconnect(\$socket) {
			Logger::log(__CLASS__, "User departed...");
			
			//# If you want some other disconnect logic here, you can create it.
	}
}
?>
EOM;
		return $Example;
	}
}
?>