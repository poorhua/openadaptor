<?xml version="1.0"?>
<!--
    [[
    Copyright (C) 2006 The Software Conservancy as Trustee. All rights
    reserved.

    Permission is hereby granted, free of charge, to any person obtaining a
    copy of this software and associated documentation files (the
    "Software"), to deal in the Software without restriction, including
    without limitation the rights to use, copy, modify, merge, publish,
    distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to
    the following conditions:

    The above copyright notice and this permission notice shall be included
    in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
    LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
    OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
    WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

    Nothing in this notice shall be deemed to grant any rights to
    trademarks, copyrights, patents, trade secrets or any other intellectual
    property of the licensor or any contributor except as expressly stated
    herein. No patent license is granted separate from the Software, for
    code that you delete from the Software, or for combinations of the
    Software with other software or hardware.
    ]]

    $HeadURL: https://openadaptor3.openadaptor.org/svn/openadaptor3/trunk/example/tools/xslt/index.xsl $

    @author Andrew Shire

    Used to generate "cookbook/index.html" (main entry point to cookbook).
  -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/TR/xhtml1/strict"
                xmlns:beans="http://www.springframework.org/schema/beans">

<xsl:import href="adaptorDoc.xsl"/>

<xsl:param name="oaVersion"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
    <meta content="text/html; charset=iso-8859-15" http-equiv="Content-Type"/>
    <style type="text/css">
        html { background: white }
        body {
            background: white;
            color: black;
            font-family: Arial, Helvetica, san-serif
        }
        td { text-align: left; vertical-align: top }
        th { text-align: left; vertical-align: top }
        a.th:link    {color: white; }
        a.th:visited {color: white; }
    </style>
    <title>OpenAdaptor 3: Config Examples Index</title>
</head>

<body>
    <h1>OpenAdaptor 3: Config Examples Index</h1>
    <b><xsl:value-of select="$oaVersion"/></b>

    <p>
        
    </p>

    <table bgcolor="#CCCCCC" border="1">
        <colgroup>
            <col bgcolor="#CCCCCC"/>
            <col bgcolor="#FFFFFF"/>
            <col bgcolor="#CCCCCC"/>
            <col bgcolor="#FFFFFF"/>
        </colgroup>
        <tr bgcolor="#000099">
            <th><font color="white">Index</font></th>
            <th><font color="white">Description</font></th>
        </tr>
        <tr>
            <td><a href="./config2beans.html">ConfigToBeans</a></td>
            <td>
                <p>
                    Lists all config examples (including those in tutorial).
                </p>
                <p>
                    For each config example it links to:
                </p>
                <ul>
                    <li>autogenerated HTML documentation</li>
                    <li>XML file</li>
                    <li>clickable graphical node map</li>
                </ul>
                <p>
                    For each config example it lists all of the bean classes it uses
                    (linking to each class in <a href="./beans2config.html">beans2config.html</a>).
                </p>
            </td>
        </tr>
        <tr>
            <td><a href="./beans2config.html">BeansToConfig</a></td>
            <td>
                <p>
                    Lists all bean classes used in the config examples.
                </p>
                <p>
                    For each bean class it links to its JavaDoc.
                </p>
                <p>
                    For each bean class it lists the config examples that use it
                    (linking to each example in <a href="./config2beans.html">config2beans.html</a>).
                </p>
            </td>
        </tr>
        <tr>
            <td><a href="./allimages.html">ImagesIndex</a></td>
            <td>
                <p>
                    Lists all config examples (including those in tutorial).
                </p>
                <p>
                    For each config example it displays a clickable graphical node map.
                </p>
            </td>
        </tr>
        <tr>
            <td>
                <table>
                    <tr><td><a href="../readme.html">../readme.html</a></td></tr>
                </table>
            </td>
            <td>
                <p>
                    Explains how to run adaptors including these examples.
                </p>
            </td>
        </tr>
        <tr>
            <td>
                <table>
                    <tr><td><a href="../tutorial/index.html">../tutorial/index.html</a></td></tr>
                </table>
            </td>
            <td>
                <p>
                    Tutorial example.
                </p>
            </td>
        </tr>
    </table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
