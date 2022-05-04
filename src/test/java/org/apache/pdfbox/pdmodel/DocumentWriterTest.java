package org.apache.pdfbox.pdmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;

class DocumentWriterTest {

	private static final String OWNER_PASSWORD = "OWNER_PASSWORD";

	private static Method METHOD_INIT, METHOD_GET_WIDTH, METHOD_GET_FONTS, METHOD_GET_PAGE_SIZE_MAP, METHOD_CAST,
			METHOD_ADD_ACTION_LISTENER, METHOD_CREATE_PROTECTION_POLICY, METHOD_SET_TEXT, METHOD_TEST_AND_GET2,
			METHOD_TEST_AND_GET3, METHOD_GET_FOREGROUND, METHOD_VALUE_OF, METHOD_GET, METHOD_GET_SELECTED_ITEM,
			METHOD_SET_WIDTH, METHOD_GET_TEXT, METHOD_CREATE_ACCESS_PERMISSION, METHOD_SET_ACCESSIBLE, METHOD_TO_LINES,
			METHOD_CHECK_PASSWORD, METHOD_PACK, METHOD_SET_VISIBLE, METHOD_CREATE_PROPERTIES_DIALOG,
			METHOD_CREATE_PERMISSION_DIALOG = null;

	@BeforeAll
	static void beforeAll() throws ReflectiveOperationException {
		//
		final Class<?> clz = DocumentWriter.class;
		//
		(METHOD_INIT = clz.getDeclaredMethod("init", Container.class)).setAccessible(true);
		//
		(METHOD_GET_WIDTH = clz.getDeclaredMethod("getWidth", Dimension2D.class, Double.TYPE)).setAccessible(true);
		//
		(METHOD_GET_FONTS = clz.getDeclaredMethod("getFonts", Field[].class)).setAccessible(true);
		//
		(METHOD_GET_PAGE_SIZE_MAP = clz.getDeclaredMethod("getPageSizeMap", Field[].class)).setAccessible(true);
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
		(METHOD_ADD_ACTION_LISTENER = clz.getDeclaredMethod("addActionListener", ActionListener.class,
				AbstractButton[].class)).setAccessible(true);
		//
		(METHOD_CREATE_PROTECTION_POLICY = clz.getDeclaredMethod("createProtectionPolicy", String.class, String.class,
				AccessPermission.class)).setAccessible(true);
		//
		(METHOD_SET_TEXT = clz.getDeclaredMethod("setText", JTextComponent.class, String.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_GET2 = clz.getDeclaredMethod("testAndGet", Predicate.class, Object[].class))
				.setAccessible(true);
		//
		(METHOD_TEST_AND_GET3 = clz.getDeclaredMethod("testAndGet", Predicate.class, Object.class, Supplier.class))
				.setAccessible(true);
		//
		(METHOD_GET_FOREGROUND = clz.getDeclaredMethod("getForeground", Component.class)).setAccessible(true);
		//
		(METHOD_VALUE_OF = clz.getDeclaredMethod("valueOf", String.class)).setAccessible(true);
		//
		(METHOD_GET = clz.getDeclaredMethod("get", Map.class, Object.class)).setAccessible(true);
		//
		(METHOD_GET_SELECTED_ITEM = clz.getDeclaredMethod("getSelectedItem", ComboBoxModel.class)).setAccessible(true);
		//
		(METHOD_SET_WIDTH = clz.getDeclaredMethod("setWidth", Integer.TYPE, Component[].class)).setAccessible(true);
		//
		(METHOD_GET_TEXT = clz.getDeclaredMethod("getText", JTextComponent.class)).setAccessible(true);
		//
		(METHOD_CREATE_ACCESS_PERMISSION = clz.getDeclaredMethod("createAccessPermission", Object.class, Field[].class))
				.setAccessible(true);
		//
		(METHOD_SET_ACCESSIBLE = clz.getDeclaredMethod("setAccessible", AccessibleObject.class)).setAccessible(true);
		//
		(METHOD_TO_LINES = clz.getDeclaredMethod("toLines", String.class, PDFont.class, Integer.class, Float.TYPE))
				.setAccessible(true);
		//
		(METHOD_CHECK_PASSWORD = clz.getDeclaredMethod("checkPassword", CharSequence.class, CharSequence.class))
				.setAccessible(true);
		//
		(METHOD_PACK = clz.getDeclaredMethod("pack", Window.class)).setAccessible(true);
		//
		(METHOD_SET_VISIBLE = clz.getDeclaredMethod("setVisible", Component.class, Boolean.TYPE)).setAccessible(true);
		//
		(METHOD_CREATE_PROPERTIES_DIALOG = clz.getDeclaredMethod("createPropertiesDialog")).setAccessible(true);
		//
		(METHOD_CREATE_PERMISSION_DIALOG = clz.getDeclaredMethod("createPermissionDialog")).setAccessible(true);
		//
	}

	private DocumentWriter instance = null;

	@BeforeEach
	void beforeEach() throws ReflectiveOperationException {
		//
		final Constructor<DocumentWriter> constructor = DocumentWriter.class.getDeclaredConstructor();
		if (constructor != null && !constructor.isAccessible()) {
			constructor.setAccessible(true);
		}
		instance = constructor != null ? constructor.newInstance() : null;
		//
	}

	@Test
	void testInit() {
		Assertions.assertDoesNotThrow(() -> init(null));
	}

	private void init(final Container container) throws Throwable {
		try {
			METHOD_INIT.invoke(instance, container);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testActionPerformed() throws IllegalAccessException {
		//
		Assertions.assertDoesNotThrow(() -> instance.actionPerformed(new ActionEvent("", 1, null)));
		//
		final AbstractButton btnCopy = new JButton();
		FieldUtils.writeDeclaredField(instance, "btnCopy", btnCopy, true);
		Assertions.assertDoesNotThrow(() -> instance.actionPerformed(new ActionEvent(btnCopy, 1, null)));
		//
	}

	@Test
	void testGetWidth() throws Throwable {
		//
		final int width = 100;
		Assertions.assertEquals(width, getWidth(null, width));
		//
	}

	private static double getWidth(final Dimension2D instance, final double defaultValue) throws Throwable {
		try {
			final Object obj = METHOD_GET_WIDTH.invoke(null, instance, defaultValue);
			if (obj instanceof Double) {
				return ((Double) obj).doubleValue();
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

	@Test
	void testGetFonts() throws Throwable {
		//
		Assertions.assertNull(getFonts(null));
		Assertions.assertNull(getFonts(new Field[] { null }));
		//
	}

	private static PDFont[] getFonts(final Field[] fs) throws Throwable {
		try {
			final Object obj = METHOD_GET_FONTS.invoke(null, (Object) fs);
			if (obj == null) {
				return null;
			} else if (obj instanceof PDFont[]) {
				return (PDFont[]) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetPageSizeMap() throws Throwable {
		//
		Assertions.assertNull(getPageSizeMap(null));
		Assertions.assertNull(getPageSizeMap(new Field[] { null }));
		//
	}

	private static Map<String, PDRectangle> getPageSizeMap(final Field[] fs) throws Throwable {
		try {
			final Object obj = METHOD_GET_PAGE_SIZE_MAP.invoke(null, (Object) fs);
			if (obj == null) {
				return null;
			} else if (obj instanceof Map) {
				return (Map) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCast() throws Throwable {
		Assertions.assertNull(cast(null, null));
	}

	private static <T> T cast(final Class<T> clz, final Object instance) throws Throwable {
		try {
			return (T) METHOD_CAST.invoke(null, clz, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGddActionListener() {
		//
		Assertions.assertDoesNotThrow(() -> addActionListener(null, (AbstractButton[]) null));
		Assertions.assertDoesNotThrow(() -> addActionListener(null, (AbstractButton) null));
		//
	}

	private static void addActionListener(final ActionListener actionListener, final AbstractButton... bs)
			throws Throwable {
		try {
			METHOD_ADD_ACTION_LISTENER.invoke(null, actionListener, bs);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCreateProtectionPolicy() throws Throwable {
		//
		Assertions.assertEquals(
				"StandardProtectionPolicy[ownerPassword=<null>,permissions=<null>,userPassword=<null>,encryptionKeyLength=128,preferAES=true]",
				ToStringBuilder
						.reflectionToString(createProtectionPolicy(null, null, null), ToStringStyle.SHORT_PREFIX_STYLE)
						.replaceAll("\\@[\\w|\\d]+", ""));
		//
		final String userPassword = "userPassword";
		final AccessPermission accessPermission = new AccessPermission();
		//
		Assertions.assertEquals(String.format(
				"StandardProtectionPolicy[ownerPassword=OWNER_PASSWORD,permissions=org.apache.pdfbox.pdmodel.encryption.AccessPermission,userPassword=userPassword,encryptionKeyLength=128,preferAES=true]",
				OWNER_PASSWORD, accessPermission, userPassword),
				ToStringBuilder
						.reflectionToString(createProtectionPolicy(OWNER_PASSWORD, userPassword, accessPermission),
								ToStringStyle.SHORT_PREFIX_STYLE)
						.replaceAll("\\@[\\w|\\d]+", ""));
		//
	}

	private static ProtectionPolicy createProtectionPolicy(final String ownerPassword, final String userPassword,
			final AccessPermission accessPermission) throws Throwable {
		try {
			final Object obj = METHOD_CREATE_PROTECTION_POLICY.invoke(null, ownerPassword, userPassword,
					accessPermission);
			if (obj == null) {
				return null;
			} else if (obj instanceof ProtectionPolicy) {
				return (ProtectionPolicy) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetText() {
		//
		Assertions.assertDoesNotThrow(() -> setText(null, null));
		Assertions.assertDoesNotThrow(() -> setText(new JTextField(), null));
		//
	}

	private static void setText(final JTextComponent instance, final String text) throws Throwable {
		try {
			METHOD_SET_TEXT.invoke(null, instance, text);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testTestAndGet() throws Throwable {
		//
		Assertions.assertNull(testAndGet(null, (Object[]) null));
		Assertions.assertNull(testAndGet(null, (Object) null));
		Assertions.assertNull(testAndGet(Predicates.alwaysFalse(), (Object) null));
		//
		Assertions.assertSame(OWNER_PASSWORD, testAndGet(Predicates.alwaysTrue(), OWNER_PASSWORD));
		//
		Assertions.assertNull(testAndGet(null, null, null));
		Assertions.assertNull(testAndGet(x -> x != null, null, null));
		//
	}

	private static <T> T testAndGet(final Predicate<T> predicate, final T... items) throws Throwable {
		try {
			return (T) METHOD_TEST_AND_GET2.invoke(null, predicate, items);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static <T> T testAndGet(final Predicate<Object> predicate, final T value, final Supplier<T> supplier)
			throws Throwable {
		try {
			return (T) METHOD_TEST_AND_GET3.invoke(null, predicate, value, supplier);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetForeground() throws Throwable {
		Assertions.assertNull(getForeground(null));
	}

	private static Color getForeground(final Component instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_FOREGROUND.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Color) {
				return (Color) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testValueOf() throws Throwable {
		//
		Assertions.assertNull(valueOf(null));
		Assertions.assertNull(valueOf(""));
		//
		final int one = 1;
		Assertions.assertSame(Integer.valueOf(one), valueOf(Integer.toString(one)));
		//
	}

	private static Integer valueOf(final String instance) throws Throwable {
		try {
			final Object obj = METHOD_VALUE_OF.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Integer) {
				return (Integer) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGet() throws Throwable {
		//
		Assertions.assertNull(get(null, null));
		Assertions.assertSame(OWNER_PASSWORD, get(Collections.singletonMap(null, OWNER_PASSWORD), null));
		//
	}

	private static <V> V get(final Map<?, V> instance, final Object key) throws Throwable {
		try {
			return (V) METHOD_GET.invoke(null, instance, key);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetSelectedItem() throws Throwable {
		//
		Assertions.assertNull(getSelectedItem(null));
		//
		Assertions.assertSame(OWNER_PASSWORD,
				getSelectedItem(new DefaultComboBoxModel<>(new Object[] { OWNER_PASSWORD })));
		//
	}

	private static Object getSelectedItem(final ComboBoxModel<?> instance) throws Throwable {
		try {
			return (Object) METHOD_GET_SELECTED_ITEM.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetWidth() {
		//
		Assertions.assertDoesNotThrow(() -> setWidth(0, (Component[]) null));
		Assertions.assertDoesNotThrow(() -> setWidth(0, (Component) null));
		//
	}

	private static void setWidth(final int width, final Component... cs) throws Throwable {
		try {
			METHOD_SET_WIDTH.invoke(null, width, cs);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetText() throws Throwable {
		Assertions.assertEquals(OWNER_PASSWORD, getText(new JTextField(OWNER_PASSWORD)));
	}

	private static String getText(final JTextComponent instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_TEXT.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCreateAccessPermission() throws Throwable {
		//
		final Function<Object, String> toString = item -> ToStringBuilder.reflectionToString(item,
				ToStringStyle.SHORT_PREFIX_STYLE);
		//
		Assertions.assertEquals("AccessPermission[bytes=-4,readOnly=false]",
				toString.apply(createAccessPermission(null, null)));
		//
		final Field field = DocumentWriter.class.getDeclaredField("canAssembleDocument");
		final Field[] fields = new Field[] { null, String.class.getDeclaredField("value"), field };
		//
		Assertions.assertEquals("AccessPermission[bytes=-4,readOnly=false]",
				toString.apply(createAccessPermission(null, fields)));
		Assertions.assertEquals("AccessPermission[bytes=-4,readOnly=false]",
				toString.apply(createAccessPermission(instance, fields)));
		//
		setAccessible(field);
		//
		final ComboBoxModel<?> canAssembleDocument = new DefaultComboBoxModel<>(
				new Boolean[] { null, Boolean.FALSE, Boolean.TRUE });
		field.set(instance, canAssembleDocument);
		//
		Assertions.assertEquals("AccessPermission[bytes=-4,readOnly=false]",
				toString.apply(createAccessPermission(null, fields)));
		Assertions.assertEquals("AccessPermission[bytes=-4,readOnly=false]",
				toString.apply(createAccessPermission(instance, fields)));
		//
		canAssembleDocument.setSelectedItem(Boolean.FALSE);
		Assertions.assertEquals("AccessPermission[bytes=-4,readOnly=false]",
				toString.apply(createAccessPermission(null, fields)));
		Assertions.assertEquals("AccessPermission[bytes=-1028,readOnly=false]",
				toString.apply(createAccessPermission(instance, fields)));
		//
	}

	private static AccessPermission createAccessPermission(final Object instance, final Field[] fs) throws Throwable {
		try {
			final Object obj = METHOD_CREATE_ACCESS_PERMISSION.invoke(null, instance, fs);
			if (obj == null) {
				return null;
			} else if (obj instanceof AccessPermission) {
				return (AccessPermission) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetAccessible() {
		Assertions.assertDoesNotThrow(() -> setAccessible(null));
	}

	private static void setAccessible(final AccessibleObject instance) throws Throwable {
		try {
			METHOD_SET_ACCESSIBLE.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testToLines() throws Throwable {
		//
		Assertions.assertNull(toLines(null, null, null, 0));
		Assertions.assertNull(toLines("", null, null, 0));
		//
		Assertions.assertEquals(Arrays.asList(OWNER_PASSWORD), toLines(OWNER_PASSWORD, null, null, 0));
		Assertions.assertEquals(Arrays.asList(OWNER_PASSWORD), toLines(OWNER_PASSWORD, null, 1, 0));
		//
		final String line2 = "line 2";
		Assertions.assertEquals(Arrays.asList(OWNER_PASSWORD, line2),
				toLines(OWNER_PASSWORD + "\n" + line2, null, 1, 0));
		//
	}

	private static List<String> toLines(final String input, final PDFont font, final Integer fontSize,
			final float width) throws Throwable {
		try {
			final Object obj = METHOD_TO_LINES.invoke(null, input, font, fontSize, width);
			if (obj == null) {
				return null;
			} else if (obj instanceof List) {
				return (List) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCheckPassword() throws Throwable {
		//
		Assertions.assertTrue(checkPassword(null, null));
		Assertions.assertTrue(checkPassword(null, ""));
		Assertions.assertTrue(checkPassword("", null));
		Assertions.assertTrue(checkPassword("", ""));
		//
		Assertions.assertFalse(checkPassword(" ", null));
		Assertions.assertFalse(checkPassword(null, " "));
		Assertions.assertTrue(checkPassword(" ", " "));
		//
	}

	private static boolean checkPassword(final CharSequence password1, final CharSequence password2) throws Throwable {
		try {
			final Object obj = METHOD_CHECK_PASSWORD.invoke(null, password1, password2);
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testPack() {
		//
		Assertions.assertDoesNotThrow(() -> pack(null));
		Assertions.assertDoesNotThrow(() -> pack(new JFrame()));
		//
	}

	private static void pack(final Window instance) throws Throwable {
		try {
			METHOD_PACK.invoke(null, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetVisible() {
		//
		Assertions.assertDoesNotThrow(() -> setVisible(null, true));
		//
		if (!GraphicsEnvironment.isHeadless()) {
			Assertions.assertDoesNotThrow(() -> setVisible(new JFrame(), true));
		}
		//
	}

	private static void setVisible(final Component instance, final boolean flag) throws Throwable {
		try {
			METHOD_SET_VISIBLE.invoke(null, instance, flag);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCreatePropertiesDialog() throws Throwable {
		//
		final JDialog instance1 = createPropertiesDialog();
		Assertions.assertNotNull(instance1);
		Assertions.assertNotSame(instance1, createPropertiesDialog());
		//
	}

	private JDialog createPropertiesDialog() throws Throwable {
		try {
			final Object obj = METHOD_CREATE_PROPERTIES_DIALOG.invoke(instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof JDialog) {
				return (JDialog) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCreatePermissionDialog() throws Throwable {
		//
		final JDialog instance1 = createPermissionDialog();
		Assertions.assertNotNull(instance1);
		Assertions.assertNotSame(instance1, createPermissionDialog());
		//
	}

	private JDialog createPermissionDialog() throws Throwable {
		try {
			final Object obj = METHOD_CREATE_PERMISSION_DIALOG.invoke(instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof JDialog) {
				return (JDialog) obj;
			}
			throw new Throwable(toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}