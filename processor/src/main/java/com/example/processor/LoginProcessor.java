package com.example.processor;

import com.example.annotation.CheckLoginLogic;
import com.example.annotation.NeedLogin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class LoginProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(NeedLogin.class.getCanonicalName());
        set.add(CheckLoginLogic.class.getCanonicalName());
        return set;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> checkLoginAnnotations = roundEnv.getElementsAnnotatedWith(CheckLoginLogic.class);
        if (!checkLoginAnnotations.isEmpty()) {
            processCheckLoginLogic(checkLoginAnnotations);
        }
        Set<? extends Element> needLoginAnnotations = roundEnv.getElementsAnnotatedWith(NeedLogin.class);
        if (!needLoginAnnotations.isEmpty()) {
            processNeedLogin(needLoginAnnotations);
        }
        return true;
    }

    private void processCheckLoginLogic(Set<? extends Element> elms) {
        for (Element e : elms) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "CheckLoginLogic: " + e);
            if (e.getKind() == ElementKind.METHOD) {
                ExecutableElement executableElement = (ExecutableElement) e;
                List<? extends VariableElement> parameters = executableElement.getParameters();
                for (VariableElement el : parameters) {
//                    messager.printMessage(Diagnostic.Kind.NOTE, "" + (el instanceof TypeElement));
                }
            }
        }
    }

    private void processNeedLogin(Set<? extends Element> elms) {
        for (Element e : elms) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "NeedLogin: " + e);
        }
    }
}
