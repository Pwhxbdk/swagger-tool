# swagger-tool
IDEA Plugin -Generate swagger annotation

* 如果目标类上有@RestController或@Controller注解,则生成对应controller swagger注解，否则生成model swagger注解。
* 可通过选中类名、字段名、方法名生成指定swagger注解。
* 以下注释格式可自动填充到注解value中。
```
    /**
     * test
     * @desc test
     * @describe test
     * @description test
     */
    @ApiModelProperty("test test test test")
    private String test1;

    // test
    @ApiModelProperty("test")
    private String test2;

    @ApiModelProperty("")
    private String test3;
```
---
* If there is @RestController or @Controller annotation on the target class, the corresponding controller swagger annotation is generated, otherwise the model swagger annotation is generated.
* You can generate specified swagger annotations by selecting the class name, field name, and method name.
* The following annotation format can be automatically filled in the annotation value.
```
    /**
     * test
     * @desc test
     * @describe test
     * @description test
     */
    @ApiModelProperty("test test test test")
    private String test1;

    // test
    @ApiModelProperty("test")
    private String test2;

    @ApiModelProperty("")
    private String test3;
```