package si.multistar444.loader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A simple class to help load programs
 * @author multistar444
 * @version 2.0
 */
public class ProgramLoader implements Serializable
{
	
	private static final long serialVersionUID = -2280534559884511235L;

	/**
	 * Class to be loaded
	 */
	protected final String mainClass;
	
	/**
	 * The window
	 */
	protected transient JFrame window;
	
	/**
	 * Extra classes
	 */
	protected List<String> extraClases;
	
	/**
	 * Jar files to be loaded
	 */
	protected List<JarFile> extraJars;
	
	/**
	 * Basic constructor for <code>si.multistar444.loader.ProgramLoader</code>
	 * @param mainClass Class to be loaded
	 * @param icon 
	 */
	public ProgramLoader(String mainClass)
	{
		final int w = 320 * 2;
		final int h = 240 * 2;
		
		this.mainClass = mainClass;
		
		extraClases = new ArrayList<String>();
		extraJars = new ArrayList<JarFile>();
		
		window = new JFrame("Loading...");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setMinimumSize(new Dimension(w, h));
		window.setUndecorated(true);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((d.width - w) / 2, (d.height - h) / 2);
		
		BufferedImage i = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = i.createGraphics();
		{
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, w, h);
			g.setColor(Color.RED);
			g.setFont(new Font(Font.SANS_SERIF, 0, (int)(h * 0.26f)));
			g.drawString("Loading...", w / 100, h / 2 + (int)(h * 0.26f) / 2);
		}
		g.dispose();
		window.add(new Background(i), BorderLayout.CENTER);
	}
	
	/**
	 * Loads a program through <code>public static void load(String[])</code>
	 * @param arguments Input arguments for the <code>mainClass</code>
	 * @since 1.0
	 */
	public void load(String[] arguments)
	{
		window.setVisible(true);
		try
		{
			loadExtra();
			Class<?> c = Class.forName(mainClass);
			window.dispose();
			c.getDeclaredMethod("load", String[].class).invoke(this, (Object)arguments);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads a program through <code>public static void load(JFrame)</code>
	 * @param decoration Defines if the frame is decorated
	 * @since 1.1
	 */
	public void loadToFrame(boolean decoration)
	{
		window.setUndecorated(!decoration);
		window.setVisible(true);
		try
		{
			loadExtra();
			Class.forName(mainClass).getDeclaredMethod("load", JFrame.class).invoke(this, (Object)window);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @since 1.2
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 */
	protected void loadExtra() throws ClassNotFoundException, IOException
	{
		for(String clazz : extraClases)
		{
			Class.forName(clazz);
		}
		for(JarFile jar : extraJars)
		{
			ClassLoader defClassLoader = ClassLoader.getSystemClassLoader();
			URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(jar.getName()).toURI().toURL() }, defClassLoader);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if(entry.getName().endsWith(".class"))
				{
					classLoader.loadClass(entry.getName().substring(0, entry.getName().lastIndexOf(".class")).replaceAll("/", "."));
					System.out.println(entry.getName().substring(0, entry.getName().lastIndexOf(".class")).replaceAll("/", "."));
				}
			}
			classLoader.close();
		}
	}
	
	/**
	 * Loads extra classes to JVM
	 * @param classes Classes to be loaded
	 * @return Returns itself
	 */
	public ProgramLoader addExtraClasses(String... classes)
	{
		for(String clazz : classes)
		{
			extraClases.add(clazz);
		}
		return this;
	}
	
	/**
	 * Loads extra classes from jar files to JVM
	 * @param jars jar files to be loaded
	 * @return Returns itself
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @since 2.0
	 */
	public ProgramLoader addJars(URL... jarUrls) throws IOException, URISyntaxException
	{
		
		for(URL url : jarUrls)
		{
			JarFile jar = new JarFile(new File(url.toURI()));
			extraJars.add(jar);
		}
		return this;
	}
	
	/**
	 * Basic background component
	 * @author multistar444
	 * @since 1.0
	 */
	protected final class Background extends JComponent
	{
		private static final long serialVersionUID = 6745939322093483803L;
		/**
		 * Image to be rendered
		 */
		private final Image img;
		/**
		 * Basic constructor for <code>si.multistar444.loader.ProgramLoader.Background</code>
		 * @param img Image to be rendered
		 */
		public Background(Image img)
		{
			this.img = img;
		}
		
		public void paint(Graphics g)
		{
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		}
	}
}
