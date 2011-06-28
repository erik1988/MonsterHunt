package com.matejdro.bukkit.monsterhunt;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;

public class InputOutput {
private MonsterHunt plugin;
private static Connection connection;
	public InputOutput(MonsterHunt instance)
	{
		plugin = instance;
		if (!new File("plugins" + File.separator + "MonsterHunt").exists()) {
			try {
			(new File("plugins" + File.separator + "MonsterHunt")).mkdir();
			} catch (Exception e) {
			MonsterHunt.log.log(Level.SEVERE, "[MonsterHunt]: Unable to create plugins/MontsterHunt/ directory");
			}
			}
		Settings.globals = new Configuration(new File("plugins" + File.separator + "MonsterHunt" + File.separator, "global.txt"));
			
	}
	
	 public static synchronized Connection getConnection() {
	        try {
				if (connection == null || connection.isClosed()) {
				    connection = createConnection();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return connection;
	    }

	    private static Connection createConnection() {
	        try {
	            if (Settings.globals.getBoolean("Database.UseMySQL", false)) {
	                Class.forName("com.mysql.jdbc.Driver");
	                Connection ret = DriverManager.getConnection(Settings.globals.getString("Database.MySqlconn", ""), Settings.globals.getString("Database.MySqlUsername", ""), Settings.globals.getString("Database.MySqlPassword", ""));
	                ret.setAutoCommit(false);
	                return ret;
	            } else {
	                Class.forName("org.sqlite.JDBC");
	                Connection ret = DriverManager.getConnection("jdbc:sqlite:plugins" + File.separator + "MonsterHunt" + File.separator + "MonsterHunt.sqlite");
	                ret.setAutoCommit(false);
	                return ret;
	            }
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	            return null;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	    public void LoadHighScores()
	    {
	    	try {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet set = null;
				
				conn = getConnection();
				ps = conn.prepareStatement("SELECT * FROM monsterhunt_highscores");
				set = ps.executeQuery();
				//conn.commit();
				
				while (set.next())
				{
					plugin.highscore.put(set.getString(1), set.getInt(2));
									}
				
				set.close();
				ps.close();
				MonsterHunt.log.log(Level.INFO,"[MonsterHunt " + plugin.getDescription().getVersion() + "] Loaded " + String.valueOf(plugin.highscore.size()) + " High scores.");
				conn.close();
	    	} catch (SQLException e) {
				MonsterHunt.log.log(Level.SEVERE, "[MonsterHunt] Error while loading High scores! - " + e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    }
	    
	    public void UpdateHighScore(String playername, int score)
	    {
	    	try {
				Connection conn = InputOutput.getConnection();
				PreparedStatement ps = conn.prepareStatement("REPLACE INTO monsterhunt_highscores VALUES (?,?)");
				ps.setString(1, playername);
				ps.setInt(2, score);
				ps.executeUpdate();
				conn.commit();
				ps.close();
			} catch (SQLException e) {
				MonsterHunt.log.log(Level.SEVERE,"[MonsterHunt] Error while inserting new high score into DB! - " + e.getMessage() );
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	
	public void LoadSettings()
	{	
		LoadDefaults();
		
		if (!new File("plugins" + File.separator + "MonsterHunt" + File.separator, "global.txt").exists()) 
			{
				for (Entry<String, Object> e : Settings.defaults.entrySet())
				{
					Settings.globals.setProperty(e.getKey(), e.getValue());
				}
				Settings.globals.save();
			}
		
		Settings.globals.load();
		
		for (String n : Settings.globals.getString("EnabledWorlds").split(","))
		{
					MonsterHuntWorld mw = new MonsterHuntWorld(n);
					Configuration config = new Configuration(new File("plugins" + File.separator + "MonsterHunt" + File.separator,n + ".yml"));
					Settings settings = new Settings(config);
					mw.settings = settings;
				
					plugin.worlds.put(n, mw);
		}
		
		
	}
	
	public void LoadDefaults()
	{
		Settings.defaults.put("StartTime", 13000);
		Settings.defaults.put("EndTime", 23600);
		Settings.defaults.put("DeathPenalty", 30);
		Settings.defaults.put("TellTime", true);
		Settings.defaults.put("CountBows", true);
		Settings.defaults.put("EnableSignup", true);
		Settings.defaults.put("EnableHighScores", true);
		Settings.defaults.put("MinimumPointsPlace1", 1);
		Settings.defaults.put("MinimumPointsPlace2", 1);
		Settings.defaults.put("MinimumPointsPlace3", 1);
		Settings.defaults.put("MinimumPlayers", 2);
		Settings.defaults.put("StartChance", 100);
		Settings.defaults.put("SkipDays", 0);
		Settings.defaults.put("SignUpPeriodTime", 5);		
		Settings.defaults.put("EnabledWorlds", plugin.getServer().getWorlds().get(0).getName());
		Settings.defaults.put("OnlyCountMobsSpawnedOutside", false);
		Settings.defaults.put("OnlyCountMobsSpawnedOutsideHeightLimit", 0);
		Settings.defaults.put("SkipToIfFailsToStart", -1);
		Settings.defaults.put("AnnounceLead", true);
		
		Settings.defaults.put("Rewards.EnableReward", false);
		Settings.defaults.put("Rewards.EnableRewardEveryonePermission", false);
		Settings.defaults.put("Rewards.RewardEveryone", false);
		Settings.defaults.put("Rewards.NumberOfWinners", 3);
		Settings.defaults.put("Rewards.RewardParametersPlace1", "264 3");
		Settings.defaults.put("Rewards.RewardParametersPlace2", "264 2");
		Settings.defaults.put("Rewards.RewardParametersPlace3", "264 1");
		Settings.defaults.put("Rewards.RewardParametersEveryone", "264 1-1");

		for (String i : new String[]{"Zombie", "Skeleton", "Creeper", "Spider", "Ghast", "Slime", "ZombiePigman", "Giant", "TamedWolf", "WildWolf", "ElectrifiedCreeper", "Player"})
		{
			Settings.defaults.put("Value." + i + ".General", 10);
			Settings.defaults.put("Value." + i + ".Wolf", 7);
			Settings.defaults.put("Value." + i + ".Arrow", 4);
			Settings.defaults.put("Value." + i + ".283", 20);
		}
				
		Settings.defaults.put("Database.UseMySQL", false);
		Settings.defaults.put("Database.MySQLConn", "jdbc:mysql://localhost:3306/minecraft");
		Settings.defaults.put("Database.MySQLUsername", "root");
		Settings.defaults.put("Database.MySQLPassword", "password");
		
		Settings.defaults.put("Debug", false);
		
		Settings.defaults.put("Messages.StartMessage", "&2Monster Hunt have started in world <World>! Go kill those damn mobs!");
		Settings.defaults.put("Messages.FinishMessageWinners", "Sun is rising, so monster Hunt is finished in world <World>! Winners of the today's match are: [NEWLINE] 1st place: <NamesPlace1> (<PointsPlace1> points) [NEWLINE] 2nd place: <NamesPlace2> (<PointsPlace2> points) [NEWLINE] 3rd place: <NamesPlace3> (<PointsPlace3> points)" );
		Settings.defaults.put("Messages.KillMessageGeneral", "You have got <MobValue> points from killing that <MobName>. You have <Points> points so far. Keep it up!");
		Settings.defaults.put("Messages.KillMessageWolf", "You have got <MobValue> points because your wolf killed <MobName>. You have <Points> points so far. Keep it up!");
		Settings.defaults.put("Messages.KillMessageArrow", "You have got only <MobValue> points because you used bow when killing <MobName>. You have <Points> points so far. Keep it up!");
		Settings.defaults.put("Messages.RewardMessage", "Congratulations! You have received <Items>");
		Settings.defaults.put("Messages.DeathMessage","You have died, so your Monster Hunt score is reduced by 30%. Be more careful next time!");
		Settings.defaults.put("Messages.NoBowMessage", "Your kill is not counted. Stop camping with your bow and get into the fight!");
		Settings.defaults.put("Messages.SignupBeforeHuntMessage", "You have signed up for the next hunt in world <World>!");
		Settings.defaults.put("Messages.SignupAtHuntMessage", "You have signed up for the hunt in in world <World>. Now hurry and kill some monsters!");
		Settings.defaults.put("Messages.HighScoreMessage","You have reached a new high score: <Points> points!");
		Settings.defaults.put("Messages.FinishMessageNotEnoughPoints", "Sun is rising, so monster Hunt is finished in world <World>! Unfortunately nobody killed enough monsters, so there is no winner.");
		Settings.defaults.put("Messages.FinishMessageNotEnoughPlayers", "Sun is rising, so monster Hunt is finished in world <World>! Unfortunately there were not enough players participating, so there is no winner.");
		Settings.defaults.put("Messages.MessageSignUpPeriod", "Sharpen your swords, strengthen your armor and type /hunt, because Monster Hunt will begin in several mintues in world <World>!");
		Settings.defaults.put("Messages.MessageTooLateSignUp", "Sorry, you are too late to sign up. More luck next time!");
		Settings.defaults.put("Messages.MessageStartNotEnoughPlayers", "Monster Hunt was about to start, but unfortunately there were not enough players signed up. ");
		Settings.defaults.put("Messages.KillMobSpawnedInsideMessage", "Your kill was not counted. Stop grinding in caves and go outside!");
		Settings.defaults.put("Messages.MessageHuntStatusNotActive", "Hunt is currently not active anywhere");
		Settings.defaults.put("Messages.MessageHuntStatusHuntActive", "Hunt is active in <Worlds>");
		Settings.defaults.put("Messages.MessageHuntStatusLastScore", "Your last score in this world was <Points> points");
		Settings.defaults.put("Messages.MessageHuntStatusNotInvolvedLastHunt", "You were not involved in last hunt in this world");
		Settings.defaults.put("Messages.MessageHuntStatusNoKills", "You haven't killed any mob in this world's hunt yet. Hurry up!");
		Settings.defaults.put("Messages.MessageHuntStatusCurrentScore", "Your current score in this world's hunt is <Points> points! Keep it up!");
		Settings.defaults.put("Messages.MessageHuntStatusTimeReamining", "Keep up the killing! You have only <Timeleft>% of the night left in this world!");
		Settings.defaults.put("Messages.MessageLead", "<Player> has just taken over lead with <Points> points!");
		Settings.defaults.put("Messages.iConomyCurrencyName", "iConomy coin");

	}
		
	public void PrepareDB()
    {
        Connection conn = null;
        Statement st = null;
        try {
            conn = InputOutput.getConnection();
            st = conn.createStatement();
            if (Settings.globals.getBoolean("UseMySql", false))
            {
            	st.executeUpdate("CREATE TABLE IF NOT EXISTS `monsterhunt_highscores` ( `name` varchar(250) NOT NULL DEFAULT '', `highscore` integer DEFAULT NULL, PRIMARY KEY (`name`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            }
            else
            {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS \"monsterhunt_highscores\" (\"name\" VARCHAR PRIMARY KEY  NOT NULL , \"highscore\" INTEGER)");
  	
            }
                conn.commit();
        } catch (SQLException e) {
            MonsterHunt.log.log(Level.SEVERE, "[MonsterHunt]: Error while creating tables! - " + e.getMessage());
    }
    }
	
}
