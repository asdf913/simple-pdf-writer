package org.apache.pdfbox.pdmodel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import net.miginfocom.swing.MigLayout;

public class DocumentWriter implements ActionListener {

	private static final Logger LOG = Logger.getLogger(DocumentWriter.class.toString());

	private JTextComponent tfText, pfOwner, pfUser, tfFile = null;

	private AbstractButton btnExecute, btnCopy = null;

	private ComboBoxModel<PDFont> fonts = null;

	private DocumentWriter() {
	}

	private void init(final Container container) {
		//
		final String wrap = String.format("span %1$s,%2$s", 2, "wrap");
		//
		container.add(new JLabel("Text"));
		container.add(new JScrollPane(tfText = new JTextArea(10, 100)), wrap);
		//
		container.add(new JLabel("Font"));
		container.add(
				new JComboBox<>(
						fonts = new DefaultComboBoxModel<PDFont>(ArrayUtils.insert(0, getFonts(), (PDFont) null))),
				wrap);
		//
		container.add(new JLabel("Owner Password"));
		container.add(pfOwner = new JPasswordField(), wrap);
		//
		container.add(new JLabel("User Password"));
		container.add(pfUser = new JPasswordField(), wrap);
		//
		container.add(new JLabel(""));
		container.add(btnExecute = new JButton("Execute"), wrap);
		//
		container.add(new JLabel("File"));
		container.add(tfFile = new JTextField());
		container.add(btnCopy = new JButton("Copy"), "wrap");
		tfFile.setEditable(false);
		//
		addActionListener(this, btnExecute, btnCopy);
		//
		final int width = 250;
		setWidth(width - (int) btnCopy.getPreferredSize().getWidth(), tfFile);
		setWidth(width, tfText, pfOwner, pfUser);
		//
	}

	private static PDFont[] getFonts() {
		//
		List<PDFont> result = null;
		//
		final Field[] fs = PDType1Font.class.getDeclaredFields();
		Field f = null;
		PDFont font = null;
		//
		for (int i = 0; fs != null && i < fs.length; i++) {
			//
			if ((f = fs[i]) == null || !Modifier.isStatic(f.getModifiers())) {
				continue;
			} // skip null
				//
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			//
			try {
				//
				if ((font = cast(PDFont.class, f.get(null))) == null) {
					continue;
				}
				//
				if (result == null) {
					result = new ArrayList<>();
				}
				result.add(font);
				//
			} catch (final IllegalAccessException e) {
				LOG.severe(e.getMessage());
			}
			//
		} // for
			//
		return result != null ? result.toArray(new PDFont[result.size()]) : null;
		//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

	private static void addActionListener(final ActionListener actionListener, final AbstractButton... bs) {
		//
		AbstractButton b = null;
		//
		for (int i = 0; bs != null && i < bs.length; i++) {
			//
			if ((b = bs[i]) == null) {
				continue;
			} // skip null
				//
			b.addActionListener(actionListener);
			//
		} // for
			//
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		//
		final Object source = evt != null ? evt.getSource() : null;
		//
		if (Objects.deepEquals(source, btnExecute)) {
			//
			final PDFont font = cast(PDFont.class, fonts != null ? fonts.getSelectedItem() : null);
			if (font == null) {
				JOptionPane.showMessageDialog(null, "Please select a font");
				return;
			}
			//
			final PDPage page = new PDPage();
			final PDDocument document = new PDDocument();
			//
			try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				//
				document.setVersion(1.7f);
				document.addPage(page);
				//
				final String[] lines = StringUtils.split(getText(tfText));
				//
				for (int i = 0; lines != null && i < lines.length; i++) {
					//
					contentStream.beginText();
					contentStream.setFont(font, 12);
					contentStream.newLineAtOffset(10, page.getMediaBox().getHeight() - 20 * (i + 1));
					contentStream.showText(lines[i]);
					contentStream.endText();
					//
				} // for
					//
				contentStream.close();
				//
				final File file = new File("test.pdf");
				tfFile.setText(file.getAbsolutePath());
				//
				// https://pdfbox.apache.org/1.8/cookbook/encryption.html
				//
				final AccessPermission ap = new AccessPermission();
				//
				final StandardProtectionPolicy spp = new StandardProtectionPolicy(getText(pfOwner), getText(pfUser),
						ap);
				spp.setPreferAES(true);
				spp.setEncryptionKeyLength(128);
				document.protect(spp);
				//
				document.save(file);
				//
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(document);
			}
			//
		} else if (Objects.deepEquals(source, btnCopy)) {
			//
			final Toolkit toolkit = Toolkit.getDefaultToolkit();
			final Clipboard clipboard = toolkit != null ? toolkit.getSystemClipboard() : null;
			if (clipboard != null) {
				clipboard.setContents(new StringSelection(getText(tfFile)), null);
			}
			//
		} // if
			//
	}

	private static void setWidth(final int width, final Component... cs) {
		//
		Component c = null;
		Dimension preferredSize = null;
		//
		for (int i = 0; cs != null && i < cs.length; i++) {
			//
			if ((c = cs[i]) == null || (preferredSize = c.getPreferredSize()) == null) {
				continue;
			} // skip null
				//
			c.setPreferredSize(new Dimension(width, (int) preferredSize.getHeight()));
			//
		} // for
			//
	}

	private static String getText(final JTextComponent instance) {
		return instance != null ? instance.getText() : null;
	}

	public static void main(final String[] args) {
		//
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new MigLayout());
		frame.setTitle("org.apache.pdfbox.pdmodel.PDDocument Writer");
		//
		final DocumentWriter instance = new DocumentWriter();
		instance.init(frame.getContentPane());
		frame.pack();
		frame.setVisible(true);
		//
	}

}