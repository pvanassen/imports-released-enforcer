package com.synedge.enforcer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @author Paul van Assen
 */
public class EnforceImportRelease implements EnforcerRule {

    /**
     * Allows this rule to execute only when this project is a release.
     *
     * @parameter
     * @see {@link #setOnlyWhenRelease(boolean)}
     * @see {@link #isOnlyWhenRelease()}
     */
    private boolean onlyWhenRelease = false;

    public void execute(EnforcerRuleHelper helper)
            throws EnforcerRuleException {
        Log log = helper.getLog();
        log.info("Entering enforcer");
        try {
            // get the various expressions out of the helper.
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            if (project == null) {
                throw new EnforcerRuleException("There is no project");
            }
            if (onlyWhenRelease && project.getArtifact().isSnapshot()) {
                log.warn("Project is SNAPSHOT, not validating");
                return;
            }
            if (project.getDependencyManagement() == null) {
                log.info("No dependency management found. All ok");
                return;
            }
            if (project.getDependencyManagement().getDependencies() == null) {
                log.info("No dependency management dependencies found. All ok");
                return;
            }

            helper.getLog().debug("Getting POM, parse and check it");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            Document doc = db.parse(project.getFile().getAbsolutePath());
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/*[local-name()='project']/*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency']/*[local-name()='scope' and text()='import']/../*[local-name()='version']");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); i++) {
                Node version = nl.item(i);
                if (version.getTextContent().contains("-SNAPSHOT")) {
                    throw new EnforcerRuleException("Found an artifact in the import scope containing SNAPSHOT!");
                }
            }
            helper.getLog().debug("Checked them all");
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new EnforcerRuleException("Unable to parse POM", e);
        } catch (SAXException e) {
            throw new EnforcerRuleException("Unable to parse POM", e);
        } catch (IOException e) {
            throw new EnforcerRuleException("Unable to parse POM", e);
        } catch (XPathExpressionException e) {
            throw new EnforcerRuleException("Internal error in XPath parser", e);
        }
    }

    /**
     * If your rule is cacheable, you must return a unique id when parameters or conditions
     * change that would cause the result to be different. Multiple cached results are stored
     * based on their id.
     * <p>
     * The easiest way to do this is to return a hash computed from the values of your parameters.
     * <p>
     * If your rule is not cacheable, then the result here is not important, you may return anything.
     */
    public String getCacheId() {
        return "";
    }

    /**
     * This tells the system if the results are cacheable at all. Keep in mind that during
     * forked builds and other things, a given rule may be executed more than once for the same
     * project. This means that even things that change from project to project may still
     * be cacheable in certain instances.
     */
    public boolean isCacheable() {
        return false;
    }

    /**
     * If the rule is cacheable and the same id is found in the cache, the stored results
     * are passed to this method to allow double checking of the results. Most of the time
     * this can be done by generating unique ids, but sometimes the results of objects returned
     * by the helper need to be queried. You may for example, store certain objects in your rule
     * and then query them later.
     */
    public boolean isResultValid(EnforcerRule arg0) {
        return false;
    }

    public final boolean isOnlyWhenRelease() {
        return onlyWhenRelease;
    }

    public final void setOnlyWhenRelease(boolean onlyWhenRelease) {
        this.onlyWhenRelease = onlyWhenRelease;
    }

}
