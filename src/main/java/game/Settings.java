/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package main.java.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import main.java.LogToFile;

/** Class representing game settings available for the current player
 */
public class Settings implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ResourceBundle loc = null;
    public int timeForGame;
    public boolean runningChat;
    public boolean runningGameClock;
    public boolean timeLimitSet;
    public boolean upsideDown;
    public LogToFile logToFile = new LogToFile();
    
    public enum gameModes
    {

        newGame, loadGame
    }
    
    public gameModes gameMode;
    
    public Player playerWhite;
    public Player playerBlack;

    public ArrayList<Player> players = new ArrayList<>() ;
    public enum gameTypes
    {

        local, network
    }
    public gameTypes gameType;
    
    public boolean renderLabels = true;

    public Settings()
    {
        //temporally
        this.playerWhite = new Player("", "white");
        this.playerBlack = new Player("", "black");
        this.timeLimitSet = false;
        gameMode = gameModes.newGame;
    }

    /** Method to get game time set by player
     *  @return timeFofGame int with how long the game will leasts
     */
    public int getTimeForGame()
    {
        return this.timeForGame;
    }

    public static String lang(String key)
    {
        if (Settings.loc == null)
        {
            Settings.loc = PropertyResourceBundle.getBundle("main.java.resources.i18n.main_en");
            Locale.setDefault(Locale.ENGLISH);
        }
        String result = "";
        try
        {
            result = Settings.loc.getString(key);
        }
        catch (java.util.MissingResourceException exc)
        {
            result = key;
        }
       // System.out.println(Settings.loc.getLocale().toString());
         LogToFile.log(null, "INFO", Settings.loc.getLocale().toString());
        return result;
    }
}