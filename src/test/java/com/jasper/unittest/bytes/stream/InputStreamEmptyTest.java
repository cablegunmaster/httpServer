package com.jasper.unittest.bytes.stream;

import com.jasper.model.socket.models.entity.NonEmptyInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class InputStreamEmptyTest {

    @Test(expected = NonEmptyInputStream.EmptyInputStreamException.class)
    public void test_check_empty_input_stream_raises_exception_for_empty_stream() throws IOException {
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        new NonEmptyInputStream(emptyStream);
    }

    @Test
    public void test_check_empty_input_stream_ok_for_non_empty_stream_and_returned_stream_can_be_consummed_fully() throws IOException {
        String streamContent = "HELLooooô wörld";
        InputStream inputStream = IOUtils.toInputStream(streamContent, UTF_8);
        inputStream = new NonEmptyInputStream(inputStream);
        Assert.assertEquals(IOUtils.toString(inputStream, StandardCharsets.UTF_8), streamContent);
    }
}
