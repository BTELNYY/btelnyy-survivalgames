package me.btelnyy.survivalgames.misc;

import java.util.UUID;

public class CombatLoggerData
{
    public UUID attacker;

    public boolean wasPlayer;

    public double damage;

    public CombatLoggerData(UUID attacker, double damage)
    {
        this.attacker = attacker;
        this.wasPlayer = true;
        this.damage = damage;
    }

    public CombatLoggerData(double damage)
    {
        this.damage = damage;
        this.wasPlayer = false;
        this.attacker = null;
    }
}
