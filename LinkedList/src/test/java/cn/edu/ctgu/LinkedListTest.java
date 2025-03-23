package cn.edu.ctgu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedListTest.class);
    private LinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new LinkedList<>();
    }

    /* 基路径1: o=null且存在目标节点 */
    @Test
    @DisplayName("移除存在的null元素")
    void shouldRemoveNullWhenExists() {
        list.add(null);
        list.add("dummy");  // 验证多节点遍历

        LOGGER.debug(" List: {}", list);
        assertTrue(list.remove(null), "应成功移除null");

        LOGGER.debug(" list.contains: {}", list.contains(null));
        assertFalse(list.contains(null), "移除后不应包含null");

        LOGGER.debug(" List: {} ,list size : {}", list, list.size());
        assertEquals(1, list.size(), "其他元素应保持存在");  // 新增完整性检查
    }

    /* 基路径2: o=null且不存在目标节点 */
    @Test
    @DisplayName("移除不存在的null元素")
    void shouldNotRemoveNullWhenAbsent() {
        list.add("element1");
        list.add("element2");

        LOGGER.debug(" List: {}", list);
        assertFalse(list.remove(null), "无null元素时应返回false");
        LOGGER.debug(" List: {} ,list size : {}", list, list.size());
        assertEquals(2, list.size(), "原始元素不应被修改");
    }

    /* 基路径3: o≠null且存在目标节点 */
    @Test
    @DisplayName("移除存在的非null元素")
    void shouldRemoveExistingNonNullElement() {
        String target = "target";
        list.add("dummy1");
        list.add(target);
        list.add("dummy2");

        LOGGER.debug(" List: {}", list);
        assertTrue(list.remove(target), "应成功移除存在的非null元素");

        LOGGER.debug(" list.contains: {}", list.contains(target));
        assertFalse(list.contains(target), "移除后不应包含目标元素");

        LOGGER.debug(" List: {} ,list size : {}", list, list.size());
        assertEquals(2, list.size(), "其他元素应保持存在");
    }

    /* 基路径4: o≠null且不存在目标节点 */
    @Test
    @DisplayName("移除不存在的非null元素")
    void shouldNotRemoveNonExistingElement() {

        list.add("element1");
        list.add("element2");

        LOGGER.debug(" List: {}", list);
        assertFalse(list.remove("ghost"), "不存在的元素应返回false");
        LOGGER.debug(" List: {}", list);

        LOGGER.debug(" List: {} ,list size : {}", list, list.size());
        assertEquals(2, list.size(), "原始元素不应被修改");
    }

    /* 覆盖空列表的特殊路径 */
    @Test
    @DisplayName("从空列表移除null")
    void shouldFailRemoveFromEmptyList_NullCase() {
        LOGGER.debug(" List: {}", list);
        assertFalse(list.remove(null), "空列表移除null应失败");
        LOGGER.debug(" List: {} ,list size : {}", list, list.size());
    }

    @Test
    @DisplayName("从空列表移除非null")
    void shouldFailRemoveFromEmptyList_NonNullCase() {
        LOGGER.debug(" List: {}", list);
        assertFalse(list.remove("any"), "空列表移除非null应失败");
        LOGGER.debug(" List: {} ,list size : {}", list, list.size());
    }
}