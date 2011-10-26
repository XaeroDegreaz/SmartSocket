package net.smartsocket.forms;

import javax.swing.*;
import java.awt.Font;

/**
 * This class controls adding the tab interface and dedicated console for each extension.
 * Modification of this class should be done for customization of the display of the
 * extension's tabbed controller.
 * @author XaeroDegreaz
 */
public class ExtensionConsole extends JScrollPane {

	public JEditorPane logText = new JEditorPane();

	/**
	 * The constructor of the component to be displayed in the tabbed pane
	 * @param name Currently unused, but may be in the future.
	 */
	public ExtensionConsole( String name ) {
		logText.setContentType( "text/html" );
		logText.setEditable( false );
		logText.setFont( new Font( "Verdana", 0, 10 ) );
		logText.setText( ""
				+ "<html>"
				+ "<head>"
				+ "<style>"
				+ "td{text-align: left;}"
				+ "body{font-family: Verdana; font-size: 10px;}"
				+ "</style>"
				+ "</head>"
				+ "<table border='0' cellspacing='0' cellpadding='0' width='100%'>"
				+ "<tr id='marker'><td></td><td width='100%'></td></tr>"
				+ "" );
		this.setViewportView( logText );
	}
}
