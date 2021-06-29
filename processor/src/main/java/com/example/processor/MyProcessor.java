package com.example.processor;

import com.example.annotation.MyAnnotation;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class MyProcessor extends AbstractProcessor {

    // 打印日志用
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "---------->init");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 返回支持的注解类型
        Set<String> types = new HashSet<>();
        types.add(MyAnnotation.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // 返回支持的源代码版本
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 注解处理主要在该方法中
        messager.printMessage(Diagnostic.Kind.NOTE, "---------->process");
        return true;
    }
}