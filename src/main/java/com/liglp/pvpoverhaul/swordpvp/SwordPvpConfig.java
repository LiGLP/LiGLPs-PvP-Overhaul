package com.liglp.pvpoverhaul.swordpvp;

/**
 * Settings of the sword PvP sound alerts. Stored inside the main mod config.
 */
public class SwordPvpConfig {
	public static final int MAX_NOTE = 24;

	// Low health alert
	public boolean lowHealthAlert = false;
	public double lowHealthHearts = 4.0;
	public NoteSound lowHealthSound = NoteSound.PLING;
	public int lowHealthNote = 6;

	// W-tap (attack ready) alert
	public boolean wTapAlert = false;
	public NoteSound wTapSound = NoteSound.HAT;
	public int wTapNote = 18;

	public double volume = 1.0;

	/**
	 * Replaces missing or out of range values (e.g. from a hand-edited config file) with valid ones.
	 */
	public void sanitize() {
		SwordPvpConfig defaults = new SwordPvpConfig();
		if (lowHealthSound == null) lowHealthSound = defaults.lowHealthSound;
		if (wTapSound == null) wTapSound = defaults.wTapSound;
		lowHealthHearts = Math.clamp(lowHealthHearts, 0.5, 10.0);
		lowHealthNote = Math.clamp(lowHealthNote, 0, MAX_NOTE);
		wTapNote = Math.clamp(wTapNote, 0, MAX_NOTE);
		volume = Math.clamp(volume, 0.0, 1.0);
	}
}
