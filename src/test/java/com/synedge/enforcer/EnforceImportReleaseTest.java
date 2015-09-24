package com.synedge.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Paul van Assen
 */
public class EnforceImportReleaseTest {
    private final EnforceImportRelease rule = new EnforceImportRelease();

    @Test(expected = EnforcerRuleException.class)
    public void testExecuteIncorrectPom() throws Exception {
        EnforcerRuleHelper helper = createHelper("/incorrect-pom.xml");
        rule.execute(helper);
    }

    @Test
    public void testExecuteCorrectPom() throws Exception {
        EnforcerRuleHelper helper = createHelper("/correct-pom.xml");
        rule.execute(helper);
    }

    @Test
    public void testExecuteNoDepsManagementPom() throws Exception {
        EnforcerRuleHelper helper = createHelper("/pom-without-dep-management.xml");
        rule.execute(helper);
    }

    @Test
    public void testExecuteEmptyDepsManagementPom() throws Exception {
        EnforcerRuleHelper helper = createHelper("/pom-with-empty-dep-management.xml");
        rule.execute(helper);
    }

    private EnforcerRuleHelper createHelper(String pom) throws Exception {
        Model model = null;
        Reader reader;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new InputStreamReader(getClass().getResourceAsStream(pom));
            model = mavenreader.read(reader);
        } catch (Exception ignored) {
        }
        MavenProject project = new MavenProject(model);
        project.setFile(new File(getClass().getResource(pom).toURI()));
        EnforcerRuleHelper helper = mock(EnforcerRuleHelper.class);
        when(helper.evaluate(eq("${project}"))).thenReturn(project);
        Log log = mock(Log.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                System.out.println(invocation.getArguments()[0]);
                return null;
            }
        }).when(log).info(anyString());
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                System.out.println(invocation.getArguments()[0]);
                return null;
            }
        }).when(log).debug(anyString());
        when(helper.getLog()).thenReturn(log);
        return helper;
    }
}