package priv.dotjabber.tournament.service;

import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import priv.dotjabber.tournament.player.Player;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Service
public class CompilationService {
    private static final String DEFAULT_PACKAGE = "priv.dotjabber.tournament";

    @Autowired
    @Qualifier("jarPath")
    private String jarPath;

    /**
     * Method compiles given class name and sources into ready-to-go java object.
     * @param playerClassName
     * @param playerSource
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Player compile(String playerClassName, String playerSource) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaSourceCompiler javaSourceCompiler = new JavaSourceCompilerImpl();
        JavaSourceCompiler.CompilationUnit compilationUnit = javaSourceCompiler.createCompilationUnit();

        compilationUnit.addClassPathEntry(jarPath);
        compilationUnit.addJavaSource(DEFAULT_PACKAGE + "." + playerClassName, playerSource);

        ClassLoader classLoader = javaSourceCompiler.compile(compilationUnit);
        Class playerClass = classLoader.loadClass(DEFAULT_PACKAGE + "." + playerClassName);

        return (Player) playerClass.newInstance();
    }

    /**
     * Method parses given file and file name. As a return it gives new class name (unique)
     * along with source content.
     * @param fileName
     * @param fileSource
     * @return
     */
    public Pair<String, String> parse(String fileName, String fileSource) {
        String className = "Player" + DigestUtils.md5Hex(new Date().toString() + new Random().nextInt());

        // check and replace classname;
        fileSource = fileSource.replaceAll(fileName.replace(".java", ""), className);

        // check and replace package
        fileSource = fileSource.replaceAll("package ([\\.a-zA-Z0-9]+);", "package " + DEFAULT_PACKAGE + ";");

        return new ImmutablePair<>(className, fileSource);
    }
}
