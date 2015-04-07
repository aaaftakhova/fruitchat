package client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPasswordField;

class HintPwField extends JPasswordField {

	private static final long serialVersionUID = 4937944599967031994L;
	private final String hint;

	public HintPwField(final String hint) {
		super("");
		this.hint = hint;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (getText().length() == 0) {
			g.setColor(Color.GRAY);
			g.drawString(hint, 3, 15);
		}
	}
	
	@Override
	public String getText() {
		char[] c = getPassword();
		String str="";
		for (char d : c) {
			str+=d;
		}
		return str;
	}

}