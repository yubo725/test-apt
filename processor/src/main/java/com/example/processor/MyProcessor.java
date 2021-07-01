package com.example.processor;

import com.example.annotation.MyAnnotation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class MyProcessor extends AbstractProcessor {

    // 打印日志用
    private Messager messager;
    // 操作文件用
    private Filer filer;

    // 针对MainActivity会生成MainActivityViewInjector类
    private static final String CLS_SUFFIX = "ViewInjector";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    // 必须复写该方法，否则注解处理器不知道处理哪个注解
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
        // elementsAnnotatedWith集合为所有被MyAnnotation注解的元素（Element represents a program element such as a package, class, or method）
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);
        if (elementsAnnotatedWith.isEmpty()) {
            // 集合为空，不用继续处理
            return false;
        }
        // map中的key代表某个类的全路径，value存放这个类中所有被注解标记了的元素
        Map<String, List<VariableElement>> map = new HashMap<>();
        for (Element e : elementsAnnotatedWith) {
            // kind表示元素类型
            ElementKind kind = e.getKind();
            // ElementKind.FIELD表示元素是一个字段
            if (kind == ElementKind.FIELD) {
                // VariableElement表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数
                VariableElement variableElement = (VariableElement) e;
                String clsName = getFullClassName(variableElement);
                if (!map.containsKey(clsName)) {
                    map.put(clsName, new ArrayList<VariableElement>());
                }
                map.get(clsName).add(variableElement);
                messager.printMessage(Diagnostic.Kind.NOTE, "add element " + e + " in class " + clsName);
            }
        }
        if (!map.isEmpty()) {
            Map<String, JavaFile> stringListMap = generateJavaCode(map);
            if (!stringListMap.isEmpty()) {
                // 将所有类对应的新生成的Java源码写入到文件
                Iterator<String> iterator = stringListMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String clsName = iterator.next();
                    JavaFile javaFile = stringListMap.get(clsName);
                    try {
                        // 将生成的Java源码文件写入到filter，写入成功就会在项目主module的build/generated/ap_generated_sources/目录下生成源码文件
                        javaFile.writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    // 使用JavaPoet生成新的Java代码
    private Map<String, JavaFile> generateJavaCode(Map<String, List<VariableElement>> map) {
        Iterator<String> iterator = map.keySet().iterator();
        Map<String, JavaFile> resultMap = new HashMap<>();
        while (iterator.hasNext()) {
            // 类名
            String clsName = iterator.next();
            // 类中被注解的字段集合
            List<VariableElement> list = map.get(clsName);
            // 包名
            String pkgName = getPackageName(list.get(0));
            // 构造一个静态的inject方法，在其中完成View的绑定
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("inject")
                    // 修饰器为public static
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    // 给方法添加参数，参数名为activity，参数类型是Activity
                    .addParameter(ClassName.get(pkgName, clsName), "activity");
            // 遍历类中所有被注解标记的元素
            for (VariableElement e : list) {
                // 拿到被注解标记的字段名称
                String fieldName = e.getSimpleName().toString();
                // 拿到注解中的值（即View的ID值）
                int value = e.getAnnotation(MyAnnotation.class).value();
                // 给上面构造的inject方法添加View赋值代码（activity.textView = activity.findViewById(xxx)）
                methodSpecBuilder.addStatement("activity.$L = activity.findViewById($L)", fieldName, value);
            }
            // 每个类都生成对应的XXXViewInjector类
            String generatedClsName = getSimpleClassName(list.get(0)) + CLS_SUFFIX;
            // 构造XXXViewInjector类
            TypeSpec typeSpec = TypeSpec.classBuilder(generatedClsName)
                    // 类的修饰器为public
                    .addModifiers(Modifier.PUBLIC)
                    // 给这个类添加方法
                    .addMethod(methodSpecBuilder.build())
                    .build();
            // JavaFile表示一个Java源码文件
            JavaFile javaFile = JavaFile.builder(pkgName, typeSpec).build();
            resultMap.put(generatedClsName, javaFile);
        }
        return resultMap;
    }

    // 获取元素的类名（不包含包名）
    private String getSimpleClassName(VariableElement element) {
        return ((TypeElement) element.getEnclosingElement()).getSimpleName().toString();
    }

    // 获取元素所在的类名全路径（包括包名，比如com.example.testapt.MainActivity）
    private String getFullClassName(VariableElement element) {
        String packageName = getPackageName(element);
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String className = typeElement.getSimpleName().toString();
        return packageName + "." + className;
    }

    // 获取元素所在的包名
    private String getPackageName(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        return processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
    }
}