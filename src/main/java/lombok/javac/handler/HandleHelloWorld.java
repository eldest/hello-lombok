package lombok.javac.handler;


import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.HelloWorld;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;
import org.mangosdk.spi.ProviderFor;

import java.lang.reflect.Modifier;

@ProviderFor(JavacAnnotationHandler.class)
@SuppressWarnings("restriction")
public class HandleHelloWorld implements JavacAnnotationHandler<HelloWorld> {

    public boolean handle(AnnotationValues<HelloWorld> annotation, JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.markAnnotationAsProcessed(annotationNode, HelloWorld.class);
        JavacNode typeNode = annotationNode.up();

        if (notAClass(typeNode)) {
            annotationNode.addError("@HelloWorld is only supported on a class.");
            return false;
        }

        JCMethodDecl helloWorldMethod = createHelloWorld(typeNode);
        JavacHandlerUtil.injectMethod(typeNode, helloWorldMethod);
        return true;
    }

    private boolean notAClass(JavacNode typeNode) {
        JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCClassDecl) typeDecl = (JCClassDecl) typeNode.get();
        long flags = typeDecl == null ? 0 : typeDecl.mods.flags;
        return typeDecl == null || (flags & (Flags.INTERFACE | Flags.ENUM | Flags.ANNOTATION)) != 0;
    }

    public boolean isResolutionBased() {
        return false;
    }

    private JCMethodDecl createHelloWorld(JavacNode type) {
        TreeMaker treeMaker = type.getTreeMaker();

        JCModifiers modifiers = treeMaker.Modifiers(Modifier.PUBLIC);
        List<JCTypeParameter> methodGenericTypes = List.<JCTypeParameter>nil();
        JCExpression methodType = treeMaker.TypeIdent(TypeTags.VOID);
        Name methodName = type.toName("helloWorld");
        List<JCVariableDecl> methodParameters = List.<JCVariableDecl>nil();
        List<JCExpression> methodThrows = List.<JCExpression>nil();

        JCExpression printlnMethod = JavacHandlerUtil.chainDots(treeMaker, type, "System", "out", "println");
        List<JCExpression> printlnArgs = List.<JCExpression>of(treeMaker.Literal("hello world"));
        JCMethodInvocation printlnInvocation = treeMaker.Apply(List.<JCExpression>nil(), printlnMethod, printlnArgs);
        JCBlock methodBody = treeMaker.Block(0, List.<JCStatement>of(treeMaker.Exec(printlnInvocation)));

        JCExpression defaultValue = null;

        return treeMaker.MethodDef(
                modifiers, methodName, methodType, methodGenericTypes, methodParameters, methodThrows, methodBody, defaultValue);
    }


}
