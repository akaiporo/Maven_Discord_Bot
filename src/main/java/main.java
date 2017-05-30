
import java.io.IOException;

import javax.security.auth.login.LoginException;

import Commands.MessageListenerActions;
import Commands.TwitchActions;
import GraphicElements.statusFrame;
import Tools.JsonFunctions;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class main extends ListenerAdapter{
	public static JDA jda;
	public static boolean relou = true;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, IOException
           
    {
		jda = new JDABuilder(AccountType.BOT).setToken("MzExOTU3NTA2MDYwNDUxODQ0.C_j2UA.Kw_S88wmdzUD32AeNMIIjsAmiMk").buildBlocking();
		//in dev 	    MzEyOTE0MDczNDE3NDE2NzA0.DAckFg.b-F1wZ4n3jnGmrMHhekIcp00jeU
		//sausage prod  MzExOTU3NTA2MDYwNDUxODQ0.C_j2UA.Kw_S88wmdzUD32AeNMIIjsAmiMk 
		MessageListenerActions messageListener = new MessageListenerActions(jda);
		messageListener.addListener();
		 
		JsonFunctions json = new JsonFunctions("Akai");
		//json.addAllGuildMembers(jda);
		TwitchActions ta = new TwitchActions();
		System.out.println(ta.getLiveStream());
		
		statusFrame frame = new statusFrame(jda);
	
    }
}
