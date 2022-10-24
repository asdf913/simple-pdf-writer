package org.apache.pdfbox.pdmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.google.common.base.Predicates;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;

public class DocumentWriter implements ActionListener {

	private static final Logger LOG = Logger.getLogger(DocumentWriter.class.toString());

	private static final String WRAP = "wrap";

	private JTextComponent tfFontSize, tfMargin, tfText, pfOwner1, pfOwner2, pfUser1, pfUser2, tfFile, tfTitle,
			tfAuthor, tfSubject, tfKeywords, tfCreator = null;

	private AbstractButton btnColor, btnProperties, btnPermission, btnExecute, btnCopy = null;

	private ComboBoxModel<Object> pageSize = null;

	private Map<String, PDRectangle> pageSizeMap = null;

	private ComboBoxModel<PDFont> font = null;

	@Retention(value = RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.FIELD })
	private @interface AccessPermissionField {
		String methodName();
	}

	@AccessPermissionField(methodName = "setCanAssembleDocument")
	private ComboBoxModel<Boolean> canAssembleDocument = null;

	@AccessPermissionField(methodName = "setCanExtractContent")
	private ComboBoxModel<Boolean> canExtractContent = null;

	@AccessPermissionField(methodName = "setCanExtractForAccessibility")
	private ComboBoxModel<Boolean> canExtractForAccessibility = null;

	@AccessPermissionField(methodName = "setCanFillInForm")
	private ComboBoxModel<Boolean> canFillInForm = null;

	@AccessPermissionField(methodName = "setCanModify")
	private ComboBoxModel<Boolean> canModify = null;

	@AccessPermissionField(methodName = "setCanModifyAnnotations")
	private ComboBoxModel<Boolean> canModifyAnnotations = null;

	@AccessPermissionField(methodName = "setCanPrint")
	private ComboBoxModel<Boolean> canPrint = null;

	@AccessPermissionField(methodName = "setCanPrintDegraded")
	private ComboBoxModel<Boolean> canPrintDegraded = null;

	private Color color = null;

	private DocumentWriter() {
	}

	private void init(final Container container) {
		//
		final String wrap = String.format("span %1$s,%2$s", 3, WRAP);
		//
		final JLabel label = new JLabel("Page Size");
		//
		add(container, label);
		add(container,
				new JComboBox<>(pageSize = new DefaultComboBoxModel<>(
						ArrayUtils.insert(0, (pageSizeMap = getPageSizeMap()).keySet().toArray(), (Object) null))),
				wrap);
		//
		add(container, new JLabel("Font size"));
		add(container, tfFontSize = new JTextField("12"), wrap);
		//
		add(container, new JLabel("Margin"));
		add(container, tfMargin = new JTextField("10"), wrap);
		//
		add(container, new JLabel("Font"));
		add(container,
				new JComboBox<>(font = new DefaultComboBoxModel<>(ArrayUtils.insert(0, getFonts(), (PDFont) null))),
				wrap);
		//
		add(container, new JLabel("Text"));
		add(container, new JScrollPane(tfText = new JTextArea(10, 100)), wrap);
		//
		add(container, new JLabel(""));
		add(container, btnColor = new JButton("Color"), wrap);
		//
		add(container, new JLabel("Owner Password"));
		add(container, pfOwner1 = new JPasswordField());
		add(container, pfOwner2 = new JPasswordField(), wrap);
		//
		add(container, new JLabel("User Password"));
		add(container, pfUser1 = new JPasswordField());
		add(container, pfUser2 = new JPasswordField(), wrap);
		//
		add(container, new JLabel(""));
		final JPanel panel = new JPanel();
		add(panel, btnProperties = new JButton("Properties"));
		add(panel, btnPermission = new JButton("Permission"));
		add(container, panel, wrap);
		//
		add(container, new JLabel(""));
		add(container, btnExecute = new JButton("Execute"), wrap);
		//
		add(container, new JLabel("File"));
		add(container, tfFile = new JTextField(), "span 2");
		add(container, btnCopy = new JButton("Copy"), "wrap");
		tfFile.setEditable(false);
		//
		addActionListener(this, btnColor, btnProperties, btnPermission, btnExecute, btnCopy);
		//
		final int width = Math.max(250, (int) getWidth(getPreferredSize(tfText), 250));
		setWidth(width - (int) getPreferredSize(btnCopy).getWidth(), tfFile);
		setWidth(width, tfFontSize, tfMargin, tfText);
		setWidth((int) (width - getWidth(getPreferredSize(label), 0) * 3) / 2, pfOwner1, pfOwner2, pfUser1, pfUser2);
		//
	}

	private static Dimension getPreferredSize(final Component instance) {
		return instance != null ? instance.getPreferredSize() : null;
	}

	private static void add(final Container container, final Component component) {
		if (container != null) {
			container.add(component);
		}
	}

	private static void add(final Container container, final Component component, final Object object) {
		if (container != null) {
			container.add(component, object);
		}
	}

	private static double getWidth(final Dimension2D instance, final double defaultValue) {
		return instance != null ? instance.getWidth() : defaultValue;
	}

	private static PDFont[] getFonts() {
		return getFonts(PDType1Font.class.getDeclaredFields());
	}

	private static PDFont[] getFonts(final Field[] fs) {
		//
		List<PDFont> result = null;
		//
		Field f = null;
		PDFont font = null;
		//
		for (int i = 0; fs != null && i < fs.length; i++) {
			//
			if ((f = fs[i]) == null || !Modifier.isStatic(f.getModifiers())) {
				continue;
			} // skip null
				//
			setAccessible(f);
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

	private static void setAccessible(final AccessibleObject instance) {
		if (instance != null && !instance.isAccessible()) {
			instance.setAccessible(true);
		}
	}

	private static Map<String, PDRectangle> getPageSizeMap() {
		return getPageSizeMap(PDRectangle.class.getDeclaredFields());
	}

	private static Map<String, PDRectangle> getPageSizeMap(final Field[] fs) {
		//
		Map<String, PDRectangle> result = null;
		//
		Field f = null;
		PDRectangle pdRectangle = null;
		//
		for (int i = 0; fs != null && i < fs.length; i++) {
			//
			if ((f = fs[i]) == null || !Modifier.isStatic(f.getModifiers())) {
				continue;
			} // skip null
				//
			setAccessible(f);
			//
			try {
				//
				if ((pdRectangle = cast(PDRectangle.class, f.get(null))) == null) {
					continue;
				}
				//
				if (result == null) {
					result = new LinkedHashMap<>();
				}
				result.put(f.getName(), pdRectangle);
				//
			} catch (final IllegalAccessException e) {
				LOG.severe(e.getMessage());
			}
			//
		} // for
			//
		return result;
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
			final PDFont font = cast(PDFont.class, getSelectedItem(this.font));
			if (font == null) {
				JOptionPane.showMessageDialog(null, "Please select a font");
				return;
			}
			//
			final Integer fontSize = valueOf(getText(tfFontSize));
			if (fontSize == null) {
				JOptionPane.showMessageDialog(null, "Please enter a vaild font size");
				return;
			}
			//
			final Integer margin = valueOf(getText(tfMargin));
			if (margin == null) {
				JOptionPane.showMessageDialog(null, "Please enter a vaild margin");
				return;
			}
			//
			final PDRectangle pageSize = cast(PDRectangle.class, get(pageSizeMap, getSelectedItem(this.pageSize)));
			final PDPage page = pageSize != null ? new PDPage(pageSize) : new PDPage();
			final PDDocument document = new PDDocument();
			//
			try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				//
				document.setVersion(1.7f);
				document.addPage(page);
				//
				final PDRectangle mediabox = page.getMediaBox();
				float width = mediabox.getWidth() - 2 * margin.intValue();
				final List<String> lines = ObjectUtils.defaultIfNull(toLines(getText(tfText), font, fontSize, width),
						Collections.emptyList());
				//
				float startX = mediabox.getLowerLeftX() + margin.intValue();
				float startY = mediabox.getUpperRightY() - margin.intValue();
				float leading = 1.5f * fontSize;
				//
				contentStream.beginText();
				contentStream.setNonStrokingColor(
						testAndGet(Predicates.notNull(), color, getForeground(tfText), Color.BLACK));
				contentStream.setFont(font, fontSize);
				contentStream.newLineAtOffset(startX, startY);
				//
				for (final String line : lines) {
					contentStream.showText(line);
					contentStream.newLineAtOffset(0, -leading);
				} // for
					//
				contentStream.endText();
				contentStream.close();
				//
				final PDDocumentInformation documentInformation = document.getDocumentInformation();
				if (documentInformation != null) {
					documentInformation.setTitle(getText(tfTitle));
					documentInformation.setAuthor(getText(tfAuthor));
					documentInformation.setSubject(getText(tfSubject));
					documentInformation.setKeywords(getText(tfKeywords));
					documentInformation.setCreator(getText(tfCreator));
				}
				//
				final File file = new File("test.pdf");
				setText(tfFile, file.getAbsolutePath());
				//
				// https://pdfbox.apache.org/1.8/cookbook/encryption.html
				//
				if (!checkPassword(getText(pfOwner1), getText(pfOwner2))) {
					JOptionPane.showMessageDialog(null, "Owner password not matched");
					return;
				} else if (!checkPassword(getText(pfUser1), getText(pfUser2))) {
					JOptionPane.showMessageDialog(null, "User password not matched");
					return;
				}
				//
				document.protect(createProtectionPolicy(getText(pfOwner1), getText(pfUser1),
						createAccessPermission(this, getClass().getDeclaredFields())));
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
		} else if (Objects.deepEquals(source, btnColor)) {
			//
			tfText.setForeground(color = JColorChooser.showDialog(null, "Font color", null));
			//
		} else if (Objects.deepEquals(source, btnProperties)) {
			//
			final JDialog dialog = createPropertiesDialog();
			//
			pack(dialog);
			setVisible(dialog, true);
			//
		} else if (Objects.deepEquals(source, btnPermission)) {
			//
			final JDialog dialog = createPermissionDialog();
			//
			pack(dialog);
			setVisible(dialog, true);
			//
		}
		//
	}

	private static void pack(final Window instance) {
		if (instance != null) {
			instance.pack();
		}
	}

	private static void setVisible(final Component instance, final boolean flag) {
		if (instance != null) {
			instance.setVisible(flag);
		}
	}

	private JDialog createPropertiesDialog() {
		//
		final JDialog dialog = new JDialog();
		dialog.setTitle("Properties");
		dialog.setLayout(new MigLayout());
		//
		add(dialog, new JLabel("Title"));
		add(dialog, tfTitle = ObjectUtils.defaultIfNull(tfTitle, new JTextField()), WRAP);
		//
		add(dialog, new JLabel("Author"));
		add(dialog, tfAuthor = ObjectUtils.defaultIfNull(tfAuthor, new JTextField()), WRAP);
		//
		add(dialog, new JLabel("Subject"));
		add(dialog, tfSubject = ObjectUtils.defaultIfNull(tfSubject, new JTextField()), WRAP);
		//
		add(dialog, new JLabel("Keywords"));
		add(dialog, tfKeywords = ObjectUtils.defaultIfNull(tfKeywords, new JTextField()), WRAP);
		//
		add(dialog, new JLabel("Creator"));
		add(dialog, tfCreator = ObjectUtils.defaultIfNull(tfCreator, new JTextField()), WRAP);
		//
		setWidth(200, tfTitle, tfAuthor, tfSubject, tfKeywords, tfCreator);
		//
		return dialog;
		//
	}

	private JDialog createPermissionDialog() {
		//
		final JDialog dialog = new JDialog();
		dialog.setTitle("Permission");
		dialog.setLayout(new MigLayout());
		//
		final Predicate<Object> notNull = x -> x != null;
		//
		final Boolean[] booleans = new Boolean[] { null, Boolean.FALSE, Boolean.TRUE };
		//
		add(dialog, new JLabel("Assemble Document"));
		add(dialog, new JComboBox<>(canAssembleDocument = testAndGet(notNull, canAssembleDocument,
				() -> new DefaultComboBoxModel<>(booleans))), WRAP);
		//
		add(dialog, new JLabel("Extract Content"));
		add(dialog, new JComboBox<>(
				canExtractContent = testAndGet(notNull, canExtractContent, () -> new DefaultComboBoxModel<>(booleans))),
				WRAP);
		//
		add(dialog, new JLabel("Extract For Accessibility"));
		add(dialog, new JComboBox<>(canExtractForAccessibility = testAndGet(notNull, canExtractForAccessibility,
				() -> new DefaultComboBoxModel<>(booleans))), WRAP);
		//
		add(dialog, new JLabel("Fill In Form"));
		add(dialog,
				new JComboBox<>(
						canFillInForm = testAndGet(notNull, canFillInForm, () -> new DefaultComboBoxModel<>(booleans))),
				WRAP);
		//
		add(dialog, new JLabel("Modify"));
		add(dialog,
				new JComboBox<>(canModify = testAndGet(notNull, canModify, () -> new DefaultComboBoxModel<>(booleans))),
				WRAP);
		//
		add(dialog, new JLabel("Modify Annotations"));
		add(dialog, new JComboBox<>(canModifyAnnotations = testAndGet(notNull, canModifyAnnotations,
				() -> new DefaultComboBoxModel<>(booleans))), WRAP);
		//
		add(dialog, new JLabel("Print"));
		add(dialog,
				new JComboBox<>(canPrint = testAndGet(notNull, canPrint, () -> new DefaultComboBoxModel<>(booleans))),
				WRAP);
		//
		add(dialog, new JLabel("Print Degraded"));
		add(dialog, new JComboBox<>(
				canPrintDegraded = testAndGet(notNull, canPrintDegraded, () -> new DefaultComboBoxModel<>(booleans))),
				WRAP);
		//
		setWidth(200, tfTitle, tfAuthor, tfSubject, tfKeywords, tfCreator);
		//
		return dialog;
		//
	}

	private static <T> T testAndGet(final Predicate<Object> predicate, final T value, final Supplier<T> supplier) {
		//
		if (predicate == null || predicate.test(value)) {
			return value;
		}
		//
		return supplier != null ? supplier.get() : value;
		//
	}

	private static boolean checkPassword(final CharSequence password1, final CharSequence password2) {
		return (StringUtils.isEmpty(password1) && StringUtils.isEmpty(password2))
				|| StringUtils.equals(password1, password2);
	}

	private static List<String> toLines(final String input, final PDFont font, final Integer fontSize,
			final float width) throws IOException {
		//
		List<String> lines = null;
		//
		for (String text : ObjectUtils.defaultIfNull(StringUtils.split(input, "\n"), new String[0])) {
			//
			int lastSpace = -1;
			//
			while (text.length() > 0) {
				//
				int spaceIndex = text.indexOf(' ', lastSpace + 1);
				//
				if (spaceIndex < 0) {
					spaceIndex = text.length();
				}
				//
				String subString = text.substring(0, spaceIndex);
				//
				float size = intValue(fontSize, 0) * (font != null ? font.getStringWidth(subString) : 0) / 1000;
				//
				if (size > width) {
					//
					if (lastSpace < 0) {
						lastSpace = spaceIndex;
					}
					//
					if (lines == null) {
						lines = new ArrayList<>();
					}
					lines.add(subString = text.substring(0, lastSpace));
					text = text.substring(lastSpace).trim();
					lastSpace = -1;
					//
				} else if (spaceIndex == text.length()) {
					//
					if (lines == null) {
						lines = new ArrayList<>();
					}
					lines.add(text);
					text = "";
					//
				} else {
					lastSpace = spaceIndex;
				}
				//
			} // while
				//
		} // for
			//
		return lines;
		//
	}

	private static int intValue(final Number instance, final int defaultValue) {
		return instance != null ? instance.intValue() : defaultValue;
	}

	private static ProtectionPolicy createProtectionPolicy(final String ownerPassword, final String userPassword,
			final AccessPermission accessPermission) {
		//
		final StandardProtectionPolicy result = new StandardProtectionPolicy(ownerPassword, userPassword,
				accessPermission);
		result.setPreferAES(true);
		result.setEncryptionKeyLength(128);
		return result;
		//
	}

	private static AccessPermission createAccessPermission(final Object instance, final Field[] fs) {
		//
		final AccessPermission result = new AccessPermission();
		//
		Field f = null;
		AccessPermissionField apf = null;
		String methodName = null;
		//
		ComboBoxModel<?> model = null;
		Boolean b = null;
		//
		Method method = null;
		//
		for (int i = 0; fs != null && i < fs.length; i++) {
			//
			if ((f = fs[i]) == null || (apf = f.getAnnotation(AccessPermissionField.class)) == null
					|| StringUtils.isEmpty(methodName = apf.methodName())) {
				continue;
			}
			//
			setAccessible(f);
			//
			try {
				//
				if ((model = cast(ComboBoxModel.class, instance != null ? f.get(instance) : null)) != null
						&& (b = cast(Boolean.class, model.getSelectedItem())) != null
						&& (method = AccessPermission.class.getDeclaredMethod(methodName, Boolean.TYPE)) != null) {
					//
					setAccessible(method);
					//
					method.invoke(result, b);
					//
				}
				//
			} catch (final ReflectiveOperationException e) {
				LOG.severe(e.getMessage());
			}
		} // for
			//
		return result;
		//
	}

	private static void setText(final JTextComponent instance, final String text) {
		if (instance != null) {
			instance.setText(text);
		}
	}

	private static <T> T testAndGet(final Predicate<T> predicate, final T... items) {
		//
		T item = null;
		//
		for (int i = 0; items != null && i < items.length && predicate != null; i++) {
			if (predicate.test(item = items[i])) {
				return item;
			}
		} // for
			//
		return item;
		//
	}

	private static Color getForeground(final Component instance) {
		//
		boolean isJava16 = false;
		//
		try {
			//
			isJava16 = Class.forName("java.net.UnixDomainSocketAddress") != null;
			//
		} catch (final ClassNotFoundException e) {
			//
			LOG.severe(e.getMessage());
			//
		} // try
			//
		try {
			//
			final Field field = Component.class.getDeclaredField("foreground");
			//
			if (field != null && !field.isAccessible()) {
				//
				if (!isJava16) {
					//
					field.setAccessible(true);
					//
				} else if (Modifier.isStatic(field.getModifiers())) {
					//
					return cast(Color.class, Narcissus.getStaticField(field));
					//
				} else {
					//
					return cast(Color.class, instance != null ? Narcissus.getField(instance, field) : null);
					//
				} // if
					//
			} // if
				//
			return cast(Color.class, field != null && instance != null ? field.get(instance) : null);
			//
		} catch (final NoSuchFieldException | IllegalAccessException e) {
			//
			LOG.severe(e.getMessage());
			//
		} // try
			//
		return null;
		//
	}

	private static Integer valueOf(final String instance) {
		try {
			return instance != null ? Integer.valueOf(instance) : null;
		} catch (final NumberFormatException e) {
			return null;
		}
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static Object getSelectedItem(final ComboBoxModel<?> instance) {
		return instance != null ? instance.getSelectedItem() : null;
	}

	private static void setWidth(final int width, final Component... cs) {
		//
		Component c = null;
		Dimension preferredSize = null;
		//
		for (int i = 0; cs != null && i < cs.length; i++) {
			//
			if ((preferredSize = getPreferredSize(c = cs[i])) == null) {
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
		pack(frame);
		setVisible(frame, true);
		//
	}

}