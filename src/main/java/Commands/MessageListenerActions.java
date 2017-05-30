package Commands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import Tools.JsonFunctions;
import Tools.Tools;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

public class MessageListenerActions extends ListenerAdapter implements PropertyChangeListener{

	private boolean relou = true;
	private JDA jda;
	private String botname;
	
	public MessageListenerActions(JDA jda){
		if(this.jda == null){
			this.jda = jda;
		}
	}
	
	/**
	 * Ass the listener that allow the BOT to listen to the discord events
	 */
	public void addListener(){
		jda.addEventListener(new MessageListenerActions(jda));
	}
	
	public JDA getJda(){
		return this.jda;
	}
	
	/**
	 * On channelIsLiveEvent triggered, 
	 * send a message to the specified channel with a link the channel
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String channel = event.getPropertyName();
		jda.getGuildById("234661550282113026").getTextChannelById("313747898258948106").sendMessage(
				String.format("%s est maintenant Live ! Mais tout le monde s'en fou.%s https://www.twitch.tv/%s", 
						channel, System.getProperty("line.separator"), channel)
				).complete();
		
		jda.getGuildById("234661550282113026").getTextChannelById("273164492156698624").sendMessage(
				String.format("%s est maintenant Live ! Mais tout le monde s'en fou.%s https://www.twitch.tv/%s", 
						channel, System.getProperty("line.separator"), channel)
				).complete();
	}
	/**
	 * General function that analyze a message content and call the appropriate function
	 */
	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
    	
    	String botName = jda.getSelfUser().getName();

    	//Insultes "off"
    	if(event.getMessage().getContent().equals("!off") && !event.getAuthor().getName().equals(botName)){
         	this.swearOff(event);
        }
    	//insultes "on"
        if(event.getMessage().getContent().equals("!on") && !event.getAuthor().getName().equals(botName)){
        	this.swearOn(event);
        }
        //Va chercher les liens postés par un utilisateur
        if(event.getMessage().getContent().contains("!liens ") && !event.getAuthor().getName().equals(botName)){
        	String name = event.getMessage().getContent().substring(7);
        	this.searchLinks(event, name, false);
        }
        //Va chercher les liens postés par un utilisateur sans preview
        if(event.getMessage().getContent().contains("!liens-d ") && !event.getAuthor().getName().equals(botName)){
        	String name = event.getMessage().getContent().substring(9);
        	this.searchLinks(event, name, true);
        }
        //Connecter le bot a un serveur vocal
        if(event.getMessage().getContent().contains("!connect ") && !event.getAuthor().getName().equals(botName)){
        	String channel = event.getMessage().getContent().substring(9);
        	this.voiceConnection(event, channel);
        }
        //Déconnecter le bot
        if(event.getMessage().getContent().contains("!disconnect") && !event.getAuthor().getName().equals(botName)){
        	String channel = event.getMessage().getContent().substring(9);
        	this.voiceDisconnection(event, channel);
        }
        if(event.getMessage().getContent().equals("!help") 
        		&& !event.getAuthor().getName().equals(botName)){
        	
        	this.help(event);
        }
        //Fait dire "NOOT NOOT". Inutile, mais c'est pour Anna <3
        String message = event.getMessage().getContent().toLowerCase(); 
        if(message.equals("poutiou pingu !") && !event.getAuthor().getName().equals(botName)){
        	//Building a message that will be read as a /tts message
        	MessageBuilder messageBuilder = new MessageBuilder();
        	messageBuilder.setTTS(true);
        	messageBuilder.append("NOOT NOOT");
        	
        	event.getChannel().sendMessage(messageBuilder.build()).complete();
        }

        if(relou){
		    this.insulterSora(event, message);
        }
     
        if(event.getMessage().getContent().contains("!cherche ") && !event.getAuthor().getName().equals(botName)){
        	String search = message.substring(9);
        	try {
				YoutubeActions ytActions = new YoutubeActions();
				event.getChannel().sendMessage(ytActions.search(search)).complete();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        if(event.getMessage().getContent().contains("http") && !event.getChannel().getType().equals(ChannelType.PRIVATE)){
        	this.addLinks(event, event.getAuthor().getName());
        }
    }
	/**
	 * Connect the BOT to the channel
	 * @param event
	 * @param channelName -> Channel the BOT will be connected to
	 */
	private void voiceConnection(MessageReceivedEvent event, String channelName){
		if(event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)){
        	for(VoiceChannel vc : event.getGuild().getVoiceChannelsByName(channelName, true)){
        		if(vc.getName().toLowerCase().equals(channelName.toLowerCase())){
        			AudioManager manager = vc.getGuild().getAudioManager();
        			manager.openAudioConnection(vc);
        		}
        	}

    	}
    	else{
    		event.getChannel().sendMessage("Vous n'avez pas la permission de faire ça").complete();
    	}
	}
	/**
	 * Disconnect the BOT from the channel
	 * @param event
	 * @param channelName -> Channel the BOT is currently connected to
	 */
	private void voiceDisconnection(MessageReceivedEvent event, String channelName){
		if(event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)){
			for(VoiceChannel vc : event.getGuild().getVoiceChannelsByName(channelName, true)){
        		if(vc.getName().toLowerCase().equals(channelName.toLowerCase())){
        			AudioManager manager = vc.getGuild().getAudioManager();
        			manager.closeAudioConnection();
        		}
        	}
    	}
    	else{
    		event.getChannel().sendMessage("Vous n'avez pas la permission de faire ça").complete();
    	}
	}
	/**
	 * Post a message that list the BOT functions, name, and active guild
	 * @param event
	 */
	private void help(MessageReceivedEvent event){
		String message = String.format("Nom : %s %sServeur Actif : %s %sFonctions Actives :%s--> !connect [Nom du channel vocal]%s--> !disconnect [Nom du channel vocal]%s--> !on%s--> !off %s--> !help %s--> !cherche [video youtube a rechercher]%s--> !liens [nom de la personne]%s--> !liens-d [nom de la personne]%s", event.getGuild().getMember(jda.getSelfUser()).getEffectiveName(),
				System.getProperty("line.separator"), event.getGuild().getName(), System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"), System.getProperty("line.separator"),
				System.getProperty("line.separator"), System.getProperty("line.separator"), System.getProperty("line.separator"), System.getProperty("line.separator"), System.getProperty("line.separator"), System.getProperty("line.separator"));
		event.getGuild().getTextChannelById("313747898258948106").sendMessage(message).complete();
		//event.getChannel().sendMessage(message).complete();
    	if(relou){
    		//event.getChannel().sendMessage("Le mode \"insultes\" est actuellement activé.").complete();
    		event.getGuild().getTextChannelById("313747898258948106").sendMessage("Le mode \"insultes\" est actuellement activé.").complete();
    	}
    	else{
    		//event.getChannel().sendMessage("Le mode \"insultes\" est actuellement désactivé.").complete();
    		event.getGuild().getTextChannelById("313747898258948106").sendMessage("Le mode \"insultes\" actuellement désactivé.").complete();
    	}
	}
	private void swearOn(MessageReceivedEvent event){
		if(!event.getAuthor().getName().equals(botname)){
			if(event.getMember().getPermissions().contains(Permission.ADMINISTRATOR) || event.getAuthor().getId().equals("207659944483225604")){
	        	relou = true;
	        	event.getChannel().sendMessage("Mode activé.").complete();
	    	}
	    	else{
	    		event.getChannel().sendMessage("Vous n'avez pas la permission de faire ça").complete();
	    	}
		}	
	}
	private void swearOff(MessageReceivedEvent event){
		if(!event.getAuthor().getName().equals(botname)){
			if(event.getMember().getPermissions().contains(Permission.ADMINISTRATOR) || event.getAuthor().getId().equals("207659944483225604")){
		        	relou = false;
		        	event.getChannel().sendMessage("Mode désactivé.").complete();
	     	}
	     	else{
	     		event.getChannel().sendMessage("Vous n'avez pas la permission de faire ça").complete();
	     	}
		}
	}
	private void insulterSora(MessageReceivedEvent event, String message){
		String botName = jda.getSelfUser().getName();
		if((   message.contains("sora") ||
		       message.contains("@Raze Sora") ||
		       message.contains("raze sora") || 
		       message.contains("Raze") ||
		       message.contains("dora")) && !event.getAuthor().getName().equals(botName)){
			
		           event.getChannel().sendMessage("PD SORA").complete();
		}
	}
	
	/**
	 * Return all the links posted by the requested user name in a private message
	 * @param event
	 * @param name -> Requested user name
	 * @param disabled -> ifset at "true", will embed the message to disable the dicord links preview
	 */
	@SuppressWarnings("deprecation")
	private void searchLinks(MessageReceivedEvent event, String name, boolean disabled){
		for(Member m : event.getGuild().getMembers()){
			if(name.toLowerCase().equals(m.getUser().getName().toLowerCase())){
				JsonFunctions json = new JsonFunctions(name);
				StringBuilder links = new StringBuilder();
				if(disabled){
					for(Object link : Tools.jsonArrayToArrayList(json.getAuthorLinks())){
						//Putting the link between "<>" disable the preview
						links.append(String.format("<%s>", link.toString()));
						links.append(System.getProperty("line.separator"));
					}
				}
				else{
					for(Object link : Tools.jsonArrayToArrayList(json.getAuthorLinks())){
						links.append(link.toString());
						links.append(System.getProperty("line.separator"));
					}
				}
				event.getMember().getUser().openPrivateChannel().complete();
				event.getMember().getUser().getPrivateChannel().sendMessage(links.toString()).complete();
				
			}
		}
	}
	
	/**
	 * Add the link to the Key->Value[] array in the json file
	 * @param event
	 * @param name : Name of the poster (is the Key in the json file)
	 */
	private void addLinks(MessageReceivedEvent event, String name){
			for(Member m : jda.getGuildById("234661550282113026").getMembers()){
				if(name.toLowerCase().equals(m.getUser().getName().toLowerCase())){
					String message = event.getMessage().getContent(); 
					String link = message.substring(message.indexOf("http"));
					if(link.indexOf(' ') != -1){
						link = link.substring(0, link.indexOf(' '));
					}
					JsonFunctions json = new JsonFunctions(name);
					try {
						Thread.sleep(1000);
						json.addLink(link);
					}
					catch (IOException e) {
					} 
					catch (InterruptedException e) {
					}
				}
			}
	}
	
	/**
	 * Welcome the new user, and add him/her to the json file that will contains his/her links
	 */
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		event.getGuild().getPublicChannel().sendMessage(String.format("Bienvenue à %s qui vient de nous rejoindre !", event.getMember().getEffectiveName())).complete();
		new JsonFunctions(event.getMember().getUser().getName());
	}
	
	/**
	 * Prevent that a user has leave (not just disconnected) the Guild server
	 */
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		event.getGuild().getPublicChannel().sendMessage(String.format("%s a quitter le serveur. Aurevoir !", event.getMember().getEffectiveName())).complete();
	}
	
	/**
	 * Announce when someone change his nick name. 
	 * Does not work the first time someone do it
	 */
	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
		if(event.getNewNick() != null && event.getPrevNick() != null){
			event.getGuild().getTextChannelById("313747898258948106").sendMessage(String.format("%s s'appelle désormais %s !", event.getPrevNick(), event.getNewNick())).complete();
		}
		
		else if(event.getNewNick() == null){
			event.getGuild().getTextChannelById("313747898258948106").sendMessage(String.format("%s s'appelle désormais %s !", event.getPrevNick(), event.getMember().getEffectiveName())).complete();
		}
		
		/*else{
			event.getGuild().getPublicChannel().sendMessage(String.format("%s s'appelle désormais %s !", event.getMember().getEffectiveName(), event.getNewNick())).complete();
		}*/
		
	}
	
}
