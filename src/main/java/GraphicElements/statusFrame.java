package GraphicElements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Toolkit;

import javax.swing.JFrame;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.entities.Guild;

public class statusFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame mainFrame;
	Label statusLabel;
	private Status connected;
	private JDA jda;
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public statusFrame(JDA jda) {
		mainFrame = this;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.connected = jda.getStatus();
		this.jda = jda;
		this.setStatusImg();
		this.pack();
		this.setLocation((int)Math.floor(screenSize.getWidth()/2), (int)Math.floor(screenSize.getHeight()/2));
		this.setResizable(false);
		//this.setButton();
		
		this.setSize(this.getWidth(), this.getHeight()+10);
		
		this.setVisible(true);
		
	}
	private void setStatusImg(){
		statusLabel = new Label();

		
		this.getContentPane().add(statusLabel, BorderLayout.CENTER);
	
		
		if(connected == Status.CONNECTED){
			for(Guild g : jda.getGuilds()){
				statusLabel.setText(String.format("%s est connecté au serveur %s", 
						jda.getGuildById(g.getId()).getMember(jda.getSelfUser()).getEffectiveName(), 
						jda.getGuildById(g.getId()).getName()));
			}
			
			//234661550282113026 sausage
			//311953226851155968 test
			statusLabel.setBackground(Color.GREEN);
		}
		else if(connected == Status.SHUTDOWN){
			statusLabel.setText(String.format("%s est déconnecté", 
					jda.getSelfUser().getName()));
			statusLabel.setBackground(Color.RED);
		}
		else{
			statusLabel.setText("UNKNOWN STATUS");
			statusLabel.setBackground(Color.YELLOW);
		}
	}
}

