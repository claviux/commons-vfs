/* ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.vfs.test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;

/**
 * URL test cases for providers.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 1.4 $ $Date: 2003/01/23 04:41:56 $
 */
public class UrlTests
    extends AbstractProviderTestCase
{
    /**
     * Returns the capabilities required by the tests of this test case.  The
     * tests are not run if the provider being tested does not support all
     * the required capabilities.  Return null or an empty array to always
     * run the tests.
     *
     * <p>This implementation returns null.
     */
    protected Capability[] getRequiredCaps()
    {
        return new Capability[]{Capability.URI};
    }

    /**
     * Tests url.
     */
    public void testURL() throws Exception
    {
        final FileObject file = getReadFolder().resolveFile( "some-dir/" );
        final URL url = file.getURL();

        assertEquals( file.getName().getURI(), url.toExternalForm() );

        final URL parentURL = new URL( url, ".." );
        assertEquals( file.getParent().getURL(), parentURL );

        final URL rootURL = new URL( url, "/" );
        assertEquals( file.getFileSystem().getRoot().getURL(), rootURL );
    }

    /**
     * Tests content.
     */
    public void testURLContent() throws Exception
    {
        // Test non-empty file
        FileObject file = getReadFolder().resolveFile( "file1.txt" );
        assertTrue( file.exists() );

        URLConnection urlCon = file.getURL().openConnection();
        assertSameURLContent( FILE1_CONTENT, urlCon );

        // Test empty file
        file = getReadFolder().resolveFile( "empty.txt" );
        assertTrue( file.exists() );

        urlCon = file.getURL().openConnection();
        assertSameURLContent( "", urlCon );
    }

    /**
     * Tests that unknown files have no content.
     */
    public void testUnknownURL() throws Exception
    {
        // Try getting the content of an unknown file
        final FileObject unknownFile = getReadFolder().resolveFile( "unknown-file" );
        assertFalse( unknownFile.exists() );

        final URLConnection connection = unknownFile.getURL().openConnection();
        try
        {
            connection.getInputStream();
            fail();
        }
        catch ( final IOException e )
        {
            assertSameMessage( "vfs.provider/read-no-exist.error", unknownFile, e );
        }
        assertEquals( -1, connection.getContentLength() );
    }

}
