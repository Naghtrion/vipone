package me.subzero0.vipone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import me.subzero0.vipone.async.AsyncManager;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TaskVZ implements Runnable {

    private String tipo;
    private CommandSender sender;
    private Player p;
    private int dias;
    private Main plugin;
    private String grupo;
    private String key;
    private String fGrupo;
    private String[] args;

    public TaskVZ(Main plugin, String tipo) {
        this.plugin = plugin;
        this.tipo = tipo;
    }

    public TaskVZ(Main plugin, String tipo, CommandSender sender, String grupo, int dias, String key) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.sender = sender;
        this.dias = dias;
        this.grupo = grupo;
        this.key = key;
    }

    public TaskVZ(Main plugin, String tipo, CommandSender sender, String grupo) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.sender = sender;
        this.grupo = grupo;
    }

    public TaskVZ(Main plugin, String tipo, String key, CommandSender sender) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.sender = sender;
        this.key = key;
    }

    public TaskVZ(Main plugin, String tipo, CommandSender sender) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.sender = sender;
    }

    public TaskVZ(Main plugin, String tipo, Player p) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.p = p;
    }

    public TaskVZ(Main plugin, String tipo, Player p, String[] args, CommandSender sender, String grupo) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.p = p;
        this.args = args;
        this.sender = sender;
        this.grupo = grupo;
    }

    public TaskVZ(Main plugin, String tipo, Player p, String grupo) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.p = p;
        this.grupo = grupo;
    }

    public TaskVZ(Main plugin, String tipo, Player p, String grupo, String fGrupo) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.p = p;
        this.grupo = grupo;
        this.fGrupo = fGrupo;
    }

    public TaskVZ(Main plugin, String grupo, String tipo, Player p) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.p = p;
        this.grupo = grupo;
    }

    public TaskVZ(Main plugin, String tipo, Player p, int dias, String grupo) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.p = p;
        this.grupo = grupo;
        this.dias = dias;
    }

    public TaskVZ(Main plugin, String tipo, CommandSender sender, Player p) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.sender = sender;
        this.p = p;
    }

    public TaskVZ(Main plugin, String tipo, CommandSender sender, String grupo, int dias) {
        this.plugin = plugin;
        this.tipo = tipo;
        this.sender = sender;
        this.grupo = grupo;
        this.dias = dias;
    }

    @Override
    public void run() {
        switch (tipo) {
            case "addvip": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("SELECT `nome`,`" + grupo + "` FROM `vips` WHERE `" + grupo + "`!=0;");
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        PreparedStatement pst2 = con.prepareStatement("UPDATE `vips` SET `" + grupo + "`=? WHERE `nome`=?;");
                        pst2.setInt(1, (rs.getInt(grupo) + dias));
                        pst2.setString(2, rs.getString("nome"));
                        pst2.executeUpdate();
                        pst2.close();
                    }
                    sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("addvip").trim().replace("%days%", Integer.toString(dias).replace("%group%", grupo)) + ".");
                    pst.close();
                    rs.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "newkey": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    boolean ok = false;
                    while (!ok) {
                        PreparedStatement pst = con.prepareStatement("SELECT * FROM `keys` WHERE `key`='" + key + "';");
                        ResultSet rs = pst.executeQuery();
                        if (!rs.next()) {
                            ok = true;
                        } else {
                            key = plugin.FormatKey();
                        }
                        pst.close();
                        rs.close();
                    }
                    PreparedStatement pst2 = con.prepareStatement("INSERT INTO `keys` (`key`,`grupo`,`dias`) VALUES (?, ?, ?);");
                    pst2.setString(1, key);
                    pst2.setString(2, grupo);
                    pst2.setInt(3, dias);
                    pst2.executeUpdate();
                    pst2.close();
                    sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + "Key: " + ChatColor.GREEN + key + ChatColor.WHITE + " (" + grupo.toUpperCase() + ") - " + ChatColor.GREEN + dias + ChatColor.WHITE + " " + plugin.getMessage("message1") + ".");
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "trocarvip": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pstSV = con.prepareStatement("SELECT `" + grupo + "` FROM `vips` WHERE `nome`='" + sender.getName() + "';");
                    ResultSet rs = pstSV.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt(grupo) != 0) {
                            plugin.hook.setGroup((Player) sender, grupo);
                            PreparedStatement pst = con.prepareStatement("UPDATE `vips` SET `usando`=? WHERE `nome`=?;");
                            pst.setString(1, grupo);
                            pst.setString(2, sender.getName());
                            pst.executeUpdate();
                            pst.close();
                            pstSV.close();
                            rs.close();
                            sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success4") + "!");
                        } else {
                            sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error12") + "!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error6") + "!");
                    }
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "keys": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("SELECT * FROM `keys`;");
                    ResultSet rs = pst.executeQuery();
                    boolean achou = false;
                    while (rs.next()) {
                        if (!achou) {
                            sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + plugin.getMessage("list") + ":");
                        }
                        achou = true;
                        sender.sendMessage(ChatColor.WHITE + "Key: " + ChatColor.GREEN + rs.getString("key") + ChatColor.WHITE + " (" + rs.getString("grupo").toUpperCase() + ") - " + WordUtils.capitalizeFully(plugin.getMessage("days")) + ": " + ChatColor.GREEN + rs.getInt("dias"));
                    }
                    if (!achou) {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error4") + ".");
                    }
                    pst.close();
                    rs.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "delkeys": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("DELETE FROM `keys`;");
                    pst.execute();
                    pst.close();
                    con.close();
                    sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success1") + "!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "usekey": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement keyvalida = con.prepareStatement("SELECT * FROM `keys` WHERE `key`=?;");
                    keyvalida.setString(1, key);
                    ResultSet keyvalida2 = keyvalida.executeQuery();
                    if (keyvalida2.next()) {
                        String grupo = keyvalida2.getString("grupo");
                        int dias = keyvalida2.getInt("dias");
                        PreparedStatement pst = con.prepareStatement("SELECT * FROM `vips` WHERE `nome`=?;");
                        pst.setString(1, sender.getName());
                        ResultSet rs = pst.executeQuery();
                        if (plugin.usekey_global) {
                            plugin.getServer().broadcastMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success3").trim().replace("%name%", sender.getName()).replace("%group%", grupo).replace("%days%", Integer.toString(dias)) + "!");
                        } else {
                            sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success2").replace("%group%", grupo).replace("%days%", Integer.toString(dias)) + "!");
                        }
                        if (rs.next()) {
                            PreparedStatement upp = con.prepareStatement("UPDATE `vips` SET `" + grupo + "`=? WHERE `nome`=?;");
                            upp.setInt(1, (rs.getInt(grupo) + dias));
                            upp.setString(2, sender.getName());
                            upp.executeUpdate();
                            upp.close();
                            plugin.DarItensVip(((Player) sender), dias, grupo);
                        } else {
                            Calendar now = Calendar.getInstance();
                            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                            PreparedStatement upp = con.prepareStatement("INSERT INTO `vips` (`nome`,`inicio`,`usando`,`" + grupo + "`) VALUES (?, ?, ?, ?);");
                            upp.setString(1, sender.getName().trim());
                            upp.setString(2, fmt.format(now.getTime()));
                            upp.setString(3, grupo);
                            upp.setInt(4, dias);
                            upp.executeUpdate();
                            upp.close();
                            plugin.DarVip(((Player) sender), dias, grupo.trim());
                        }
                        PreparedStatement delkey = con.prepareStatement("DELETE FROM `keys` WHERE `key`=?;");
                        delkey.setString(1, key);
                        delkey.executeUpdate();
                        delkey.close();
                        if (plugin.getConfig().getBoolean("logging.usekey")) {
                            Calendar now = Calendar.getInstance();
                            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                            PreparedStatement addlog = con.prepareStatement("INSERT INTO `vipzero_log` (`comando`, `nome`,`key`,`data`,`grupo`,`dias`) VALUES ('usekey', ?, ?, ?, ?, ?);");
                            addlog.setString(1, sender.getName());
                            addlog.setString(2, key);
                            addlog.setString(3, fmt.format(now.getTime()));
                            addlog.setString(4, grupo);
                            addlog.setInt(5, dias);
                            addlog.executeUpdate();
                            addlog.close();
                        }
                        pst.close();
                        rs.close();
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error5") + "!");
                    }
                    keyvalida2.close();
                    keyvalida.close();
                    con.close();
                    plugin.using_codes.remove(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "delkey": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement keyvalida = con.prepareStatement("SELECT * FROM `keys` WHERE `key`=?;");
                    keyvalida.setString(1, key);
                    ResultSet keyvalida2 = keyvalida.executeQuery();
                    if (keyvalida2.next()) {
                        PreparedStatement delkey = con.prepareStatement("DELETE FROM `keys` WHERE `key`=?;");
                        delkey.setString(1, key);
                        delkey.executeUpdate();
                        delkey.close();
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success5").replace("%key%", key) + "!");
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error5") + "!");
                    }
                    keyvalida2.close();
                    keyvalida.close();
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "tempovip": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("SELECT * FROM `vips` WHERE `nome`=?;");
                    pst.setString(1, sender.getName());
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + plugin.getMessage("message2") + ":");
                        for (String gname : plugin.getConfig().getStringList("vip_groups")) {
                            if (rs.getInt(gname.trim()) != 0) {
                                sender.sendMessage(ChatColor.AQUA + gname.toUpperCase() + ChatColor.WHITE + " - " + plugin.getMessage("daysleft") + ": " + rs.getInt(gname.trim()) + " " + plugin.getMessage("days"));
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error6") + "!");
                    }
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "rvip": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("SELECT `nome` FROM `vips` WHERE `nome`=?;");
                    pst.setString(1, p.getName());
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        PreparedStatement pst2 = con.prepareStatement("DELETE FROM `vips` WHERE `nome`=?;");
                        pst2.setString(1, p.getName());
                        pst2.execute();
                        pst2.close();
                        plugin.hook.setGroup(p, plugin.getConfig().getString("default_group").trim());
                        plugin.getServer().broadcastMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("rvip").trim().replace("%admin%", sender.getName()).replace("%name%", p.getName()) + "!");
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + p.getName() + " " + plugin.getMessage("error9") + "!");
                    }
                    pst.close();
                    rs.close();
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "mudardias1": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("SELECT * FROM `vips` WHERE `nome`=?;");
                    pst.setString(1, p.getName());
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + rs.getString("nome") + " - " + plugin.getMessage("message2") + ":");
                        for (String gname : plugin.getConfig().getStringList("vip_groups")) {
                            if (rs.getInt(gname.trim()) != 0) {
                                sender.sendMessage(ChatColor.AQUA + gname.toUpperCase() + ChatColor.WHITE + " - " + plugin.getMessage("daysleft") + ": " + rs.getInt(gname.trim()) + " " + plugin.getMessage("days"));
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + p.getName() + " " + plugin.getMessage("error9") + "!");
                    }
                    pst.close();
                    rs.close();
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "mudardias2": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("SELECT * FROM `vips` WHERE `nome`=?;");
                    pst.setString(1, p.getName());
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        int dias = Integer.parseInt(args[2].trim());
                        if (dias > 1 && dias < 10000) {
                            PreparedStatement pst2 = con.prepareStatement("UPDATE `vips` SET `" + grupo + "`=? WHERE `nome`=?;");
                            pst2.setInt(1, dias);
                            pst2.setString(2, p.getName());
                            pst2.executeUpdate();
                            pst2.close();
                            plugin.getServer().broadcastMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("cdays").trim().replace("%admin%", sender.getName()).replace("%group%", grupo).replace("%name%", p.getName()).replace("%days%", Integer.toString(dias)) + "!");
                        } else {
                            sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("error1") + "!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + p.getName() + " " + plugin.getMessage("error9") + "!");
                    }
                    pst.close();
                    rs.close();
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "darvip": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("UPDATE `vips` SET `usando`=? WHERE `nome`=?;");
                    pst.setString(1, grupo);
                    pst.setString(2, p.getName());
                    pst.executeUpdate();
                    pst.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "tirarvip": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("UPDATE `vips` SET `" + grupo + "`=0 WHERE `nome`=?;");
                    pst.setString(1, p.getName());
                    pst.executeUpdate();
                    if (fGrupo == null) {
                        PreparedStatement pst2 = con.prepareStatement("DELETE FROM `vips` WHERE `nome`=?;");
                        pst2.setString(1, p.getName());
                        pst2.execute();
                        pst2.close();
                    } else {
                        PreparedStatement pst2 = con.prepareStatement("UPDATE `vips` SET `usando`=? WHERE `nome`=?;");
                        pst2.setString(1, fGrupo);
                        pst2.setString(2, p.getName());
                        pst2.executeUpdate();
                        pst2.close();
                    }
                    pst.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "tirarvip2": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    PreparedStatement pst = con.prepareStatement("UPDATE `vips` SET `" + grupo + "`=0 WHERE `nome`=?;");
                    pst.setString(1, p.getName());
                    pst.executeUpdate();
                    pst.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "atualizar": {
                try {
                    Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                    Player p2 = null;
                    if (p.isOnline() && p.getName() != null && p != null) {
                        p2 = p;
                    }
                    if (p2 != null) {
                        PreparedStatement pst = con.prepareStatement("SELECT * FROM `vips` WHERE `nome`=?;");
                        pst.setString(1, p2.getName());
                        ResultSet rs = pst.executeQuery();
                        if (rs.next()) {
                            Calendar now = Calendar.getInstance();
                            Calendar vip = Calendar.getInstance();
                            Calendar vip_fixo = Calendar.getInstance();
                            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                            String data = rs.getString("inicio");
                            String usando = rs.getString("usando");
                            int dias = rs.getInt(usando);
                            try {
                                vip.setTime(fmt.parse(data));
                                vip_fixo.setTime(fmt.parse(data));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            if (!fmt.format(vip.getTime()).equals(fmt.format(now.getTime()))) {
                                vip.add(Calendar.DATE, dias);
                                if (now.after(vip)) {
                                    Calendar vip2 = Calendar.getInstance();
                                    vip2.setTime(vip.getTime());
                                    Calendar temp = Calendar.getInstance();
                                    temp.setTime(vip.getTime());
                                    String fim = null;
                                    for (String n : plugin.getConfig().getStringList("vip_groups")) {
                                        if (!n.equalsIgnoreCase(usando)) {
                                            if (rs.getInt(n.trim()) != 0) {
                                                vip2.add(Calendar.DATE, rs.getInt(n.trim()));
                                                if (now.after(vip2)) {
                                                    plugin.TirarVip2(p2, n.trim());
                                                    temp.setTime(vip2.getTime());
                                                } else {
                                                    fim = n.trim();
                                                    PreparedStatement pst4 = con.prepareStatement("UPDATE `vips` SET `inicio`=? WHERE `nome`=?;");
                                                    pst4.setString(1, fmt.format(temp.getTime()));
                                                    pst4.setString(2, p2.getName());
                                                    pst4.executeUpdate();
                                                    pst4.close();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    plugin.TirarVip(p2, usando.trim(), fim);
                                } else {
                                    int total = 0;
                                    while (!fmt.format(now.getTime()).equals(fmt.format(vip_fixo.getTime()))) {
                                        vip_fixo.add(Calendar.DATE, 1);
                                        total++;
                                    }
                                    PreparedStatement pst5 = con.prepareStatement("UPDATE `vips` SET `" + usando + "`=?, `inicio`=? WHERE `nome`=?;");
                                    pst5.setInt(1, (dias - total));
                                    pst5.setString(2, fmt.format(now.getTime()));
                                    pst5.setString(3, p2.getName());
                                    pst5.executeUpdate();
                                    pst5.close();
                                }
                            }
                        } else if (plugin.getConfig().getBoolean("rvip_unlisted")) {
                            List<String> l = plugin.hook.getGroups(p2);
                            for (String n : plugin.getConfig().getStringList("vip_groups")) {
                                if (l.contains(n.trim())) {
                                    plugin.hook.setGroup(p2, plugin.getConfig().getString("default_group").trim());
                                }
                            }
                        }
                        pst.close();
                        rs.close();
                        con.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "givevip": {
                try {
                    if (!lista0.contains(p)) {
                        lista0.add(p);
                        Connection con = DriverManager.getConnection(plugin.mysql_url, plugin.mysql_user, plugin.mysql_pass);
                        PreparedStatement pst = con.prepareStatement("SELECT * FROM `vips` WHERE `nome`=?;");
                        pst.setString(1, p.getName());
                        ResultSet rs = pst.executeQuery();
                        if (plugin.usekey_global) {
                            plugin.getServer().broadcastMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success3").trim().replace("%name%", p.getName()).replace("%group%", grupo).replace("%days%", Integer.toString(dias)) + "!");
                        } else {
                            p.sendMessage(ChatColor.AQUA + "[" + plugin.getConfig().getString("server_name").trim() + "] " + ChatColor.WHITE + plugin.getMessage("success2").replace("%group%", grupo).replace("%days%", Integer.toString(dias)) + "!");
                        }
                        if (rs.next()) {
                            PreparedStatement upp = con.prepareStatement("UPDATE `vips` SET `" + grupo + "`=? WHERE `nome`=?;");
                            upp.setInt(1, (rs.getInt(grupo) + dias));
                            upp.setString(2, p.getName());
                            upp.executeUpdate();
                            upp.close();
                            plugin.DarItensVip(p, dias, grupo);
                        } else {
                            Calendar now = Calendar.getInstance();
                            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                            PreparedStatement upp = con.prepareStatement("INSERT INTO `vips` (`nome`,`inicio`,`usando`,`" + grupo + "`) VALUES (?, ?, ?, ?);");
                            upp.setString(1, p.getName().trim());
                            upp.setString(2, fmt.format(now.getTime()));
                            upp.setString(3, grupo);
                            upp.setInt(4, dias);
                            upp.executeUpdate();
                            upp.close();
                            plugin.DarVip(p, dias, grupo.trim());
                        }
                        pst.close();
                        rs.close();
                        con.close();
                        if (lista1.containsKey(p)) {
                            Thread.sleep(1000);
                            int dias_h = lista1.get(p).get(0);
                            lista1.get(p).remove(0);
                            String grupo_h = lista2.get(p).get(0);
                            lista2.get(p).remove(0);
                            TaskVZ t = new TaskVZ(plugin, "givevip", p, dias_h, grupo_h.trim());
                            AsyncManager.getInstance().addQueue(t);
                        } else {
                            lista0.remove(p);
                        }
                    } else {
                        List<Integer> l1 = lista1.get(p);
                        List<String> l2 = lista2.get(p);
                        l1.add(dias);
                        l2.add(grupo);
                        lista1.remove(p);
                        lista2.remove(p);
                        lista1.put(p, l1);
                        lista2.put(p, l2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private List<Player> lista0 = new ArrayList<Player>();
    private HashMap<Player, List<Integer>> lista1 = new HashMap<Player, List<Integer>>();
    private HashMap<Player, List<String>> lista2 = new HashMap<Player, List<String>>();
}
