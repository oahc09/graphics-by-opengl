package com.nucleus.vulkan;

public interface Vulkan10 {

    public enum Result {
        VK_SUCCESS(0),
        VK_NOT_READY(1),
        VK_TIMEOUT(2),
        VK_EVENT_SET(3),
        VK_EVENT_RESET(4),
        VK_INCOMPLETE(5),
        VK_ERROR_OUT_OF_HOST_MEMORY(-1),
        VK_ERROR_OUT_OF_DEVICE_MEMORY(-2),
        VK_VK_ERROR_INITIALIZATION_FAILED(-3),
        VK_ERROR_DEVICE_LOST(-4),
        VK_ERROR_MEMORY_MAP_FAILED(-5),
        VK_ERROR_LAYER_NOT_PRESENT(-6),
        VK_ERROR_EXTENSION_NOT_PRESENT(-7),
        VK_ERROR_FEATURE_NOT_PRESENT(-8),
        VK_ERROR_INCOMPATIBLE_DRIVER(-9),
        VK_ERROR_TOO_MANY_OBJECTS(-10),
        VK_ERROR_FORMAT_NOT_SUPPORTED(-11),
        VK_ERROR_FRAGMENTED_POOL(-12),
        VK_ERROR_OUT_OF_POOL_MEMORY(-1000069000),
        VK_ERROR_INVALID_EXTERNAL_HANDLE(-1000072003),
        VK_ERROR_SURFACE_LOST_KHR(-1000000000),
        VK_ERROR_NATIVE_WINDOW_IN_USE_KHR(-1000000001),
        VK_SUBOPTIMAL_KHR(1000001003),
        VK_ERROR_OUT_OF_DATE_KHR(-1000001004),
        VK_ERROR_INCOMPATIBLE_DISPLAY_KHR(-1000003001),
        VK_ERROR_VALIDATION_FAILED_EXT(-1000011001),
        VK_ERROR_INVALID_SHADER_NV(-1000012000),
        VK_ERROR_INVALID_DRM_FORMAT_MODIFIER_PLANE_LAYOUT_EXT(-1000158000),
        VK_ERROR_FRAGMENTATION_EXT(-1000161000),
        VK_ERROR_NOT_PERMITTED_EXT(-1000174001),
        VK_ERROR_INVALID_DEVICE_ADDRESS_EXT(-1000244000),
        VK_ERROR_FULL_SCREEN_EXCLUSIVE_MODE_LOST_EXT(-1000255000),
        VK_ERROR_OUT_OF_POOL_MEMORY_KHR(VK_ERROR_OUT_OF_POOL_MEMORY.value),
        VK_ERROR_INVALID_EXTERNAL_HANDLE_KHR(VK_ERROR_INVALID_EXTERNAL_HANDLE.value),
        VK_RESULT_MAX_ENUM(0x7FFFFFFF);

        public final int value;

        private Result(int value) {
            this.value = value;
        }

        public static Result getResult(int value) {
            for (Result r : values()) {
                if (r.value == value) {
                    return r;
                }
            }
            return null;
        }

    }

    public enum ShaderStageFlagBits {
        VK_SHADER_STAGE_VERTEX_BIT(0x00000001),
        VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT(0x00000002),
        VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT(0x00000004),
        VK_SHADER_STAGE_GEOMETRY_BIT(0x00000008),
        VK_SHADER_STAGE_FRAGMENT_BIT(0x00000010),
        VK_SHADER_STAGE_COMPUTE_BIT(0x00000020),
        VK_SHADER_STAGE_ALL_GRAPHICS(0x0000001F),
        VK_SHADER_STAGE_ALL(0x7FFFFFFF),
        VK_SHADER_STAGE_RAYGEN_BIT_NV(0x00000100),
        VK_SHADER_STAGE_ANY_HIT_BIT_NV(0x00000200),
        VK_SHADER_STAGE_CLOSEST_HIT_BIT_NV(0x00000400),
        VK_SHADER_STAGE_MISS_BIT_NV(0x00000800),
        VK_SHADER_STAGE_INTERSECTION_BIT_NV(0x00001000),
        VK_SHADER_STAGE_CALLABLE_BIT_NV(0x00002000),
        VK_SHADER_STAGE_TASK_BIT_NV(0x00000040),
        VK_SHADER_STAGE_MESH_BIT_NV(0x00000080),
        VK_SHADER_STAGE_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);

        public final int value;

        private ShaderStageFlagBits(int value) {
            this.value = value;
        }
    }

    public static class ComponentMapping {
        public ComponentSwizzle r = ComponentSwizzle.VK_COMPONENT_SWIZZLE_R;
        public ComponentSwizzle g = ComponentSwizzle.VK_COMPONENT_SWIZZLE_G;
        public ComponentSwizzle b = ComponentSwizzle.VK_COMPONENT_SWIZZLE_B;
        public ComponentSwizzle a = ComponentSwizzle.VK_COMPONENT_SWIZZLE_A;
    };

    public static class ImageSubresourceRange {
        public int aspectMask;
        public int baseMipLevel;
        public int levelCount;
        public int baseArrayLayer;
        public int layerCount;

        public ImageSubresourceRange(int aspectMask, int baseMipLevel, int levelCount, int baseArrayLayer,
                int layerCount) {
            this.aspectMask = aspectMask;
            this.baseMipLevel = baseMipLevel;
            this.levelCount = levelCount;
            this.baseArrayLayer = baseArrayLayer;
            this.layerCount = layerCount;
        }
    };

    public enum ImageAspectFlagBits {
        VK_IMAGE_ASPECT_COLOR_BIT(1),
        VK_IMAGE_ASPECT_DEPTH_BIT(2),
        VK_IMAGE_ASPECT_STENCIL_BIT(4),
        VK_IMAGE_ASPECT_METADATA_BIT(0x00000008),
        VK_IMAGE_ASPECT_PLANE_0_BIT(0x00000010),
        VK_IMAGE_ASPECT_PLANE_1_BIT(0x00000020),
        VK_IMAGE_ASPECT_PLANE_2_BIT(0x00000040),
        VK_IMAGE_ASPECT_MEMORY_PLANE_0_BIT_EXT(0x00000080),
        VK_IMAGE_ASPECT_MEMORY_PLANE_1_BIT_EXT(0x00000100),
        VK_IMAGE_ASPECT_MEMORY_PLANE_2_BIT_EXT(0x00000200),
        VK_IMAGE_ASPECT_MEMORY_PLANE_3_BIT_EXT(0x00000400),
        VK_IMAGE_ASPECT_PLANE_0_BIT_KHR(VK_IMAGE_ASPECT_PLANE_0_BIT.mask),
        VK_IMAGE_ASPECT_PLANE_1_BIT_KHR(VK_IMAGE_ASPECT_PLANE_1_BIT.mask),
        VK_IMAGE_ASPECT_PLANE_2_BIT_KHR(VK_IMAGE_ASPECT_PLANE_2_BIT.mask);

        public final int mask;

        private ImageAspectFlagBits(int mask) {
            this.mask = mask;
        }

    };

    public enum ComponentSwizzle {
        VK_COMPONENT_SWIZZLE_IDENTITY(0),
        VK_COMPONENT_SWIZZLE_ZERO(1),
        VK_COMPONENT_SWIZZLE_ONE(2),
        VK_COMPONENT_SWIZZLE_R(3),
        VK_COMPONENT_SWIZZLE_G(4),
        VK_COMPONENT_SWIZZLE_B(5),
        VK_COMPONENT_SWIZZLE_A(6);

        public final int value;

        private ComponentSwizzle(int value) {
            this.value = value;
        }

    };

    public enum ImageViewType {
        VK_IMAGE_VIEW_TYPE_1D(0),
        VK_IMAGE_VIEW_TYPE_2D(1),
        VK_IMAGE_VIEW_TYPE_3D(2),
        VK_IMAGE_VIEW_TYPE_CUBE(3),
        VK_IMAGE_VIEW_TYPE_1D_ARRAY(4),
        VK_IMAGE_VIEW_TYPE_2D_ARRAY(5),
        VK_IMAGE_VIEW_TYPE_CUBE_ARRAY(6);

        public final int value;

        private ImageViewType(int value) {
            this.value = value;
        }

        public static ImageViewType get(int value) {
            for (ImageViewType ivt : values()) {
                if (value == ivt.value) {
                    return ivt;
                }
            }
            return null;
        }

    };

    public enum PresentModeKHR {
        VK_PRESENT_MODE_IMMEDIATE_KHR(0),
        VK_PRESENT_MODE_MAILBOX_KHR(1),
        VK_PRESENT_MODE_FIFO_KHR(2),
        VK_PRESENT_MODE_FIFO_RELAXED_KHR(3),
        VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR(1000111000),
        VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR(1000111001);

        public final int value;

        private PresentModeKHR(int value) {
            this.value = value;
        }

        public static PresentModeKHR get(int value) {
            for (PresentModeKHR p : values()) {
                if (value == p.value) {
                    return p;
                }
            }
            return null;
        }

    };

    public static class SurfaceFormat {
        protected Format format;
        protected ColorSpaceKHR space;

        public SurfaceFormat(Format format, ColorSpaceKHR space) {
            this.format = format;
            this.space = space;
        }

        @Override
        public String toString() {
            return format + ", " + space;
        }

        public Format getFormat() {
            return format;
        }

        public ColorSpaceKHR getColorSpace() {
            return space;
        }

    }

    public enum Format {
        VK_FORMAT_UNDEFINED(0),
        VK_FORMAT_R4G4_UNORM_PACK8(1),
        VK_FORMAT_R4G4B4A4_UNORM_PACK16(2),
        VK_FORMAT_B4G4R4A4_UNORM_PACK16(3),
        VK_FORMAT_R5G6B5_UNORM_PACK16(4),
        VK_FORMAT_B5G6R5_UNORM_PACK16(5),
        VK_FORMAT_R5G5B5A1_UNORM_PACK16(6),
        VK_FORMAT_B5G5R5A1_UNORM_PACK16(7),
        VK_FORMAT_A1R5G5B5_UNORM_PACK16(8),
        VK_FORMAT_R8_UNORM(9),
        VK_FORMAT_R8_SNORM(10),
        VK_FORMAT_R8_USCALED(11),
        VK_FORMAT_R8_SSCALED(12),
        VK_FORMAT_R8_UINT(13),
        VK_FORMAT_R8_SINT(14),
        VK_FORMAT_R8_SRGB(15),
        VK_FORMAT_R8G8_UNORM(16),
        VK_FORMAT_R8G8_SNORM(17),
        VK_FORMAT_R8G8_USCALED(18),
        VK_FORMAT_R8G8_SSCALED(19),
        VK_FORMAT_R8G8_UINT(20),
        VK_FORMAT_R8G8_SINT(21),
        VK_FORMAT_R8G8_SRGB(22),
        VK_FORMAT_R8G8B8_UNORM(23),
        VK_FORMAT_R8G8B8_SNORM(24),
        VK_FORMAT_R8G8B8_USCALED(25),
        VK_FORMAT_R8G8B8_SSCALED(26),
        VK_FORMAT_R8G8B8_UINT(27),
        VK_FORMAT_R8G8B8_SINT(28),
        VK_FORMAT_R8G8B8_SRGB(29),
        VK_FORMAT_B8G8R8_UNORM(30),
        VK_FORMAT_B8G8R8_SNORM(31),
        VK_FORMAT_B8G8R8_USCALED(32),
        VK_FORMAT_B8G8R8_SSCALED(33),
        VK_FORMAT_B8G8R8_UINT(34),
        VK_FORMAT_B8G8R8_SINT(35),
        VK_FORMAT_B8G8R8_SRGB(36),
        VK_FORMAT_R8G8B8A8_UNORM(37),
        VK_FORMAT_R8G8B8A8_SNORM(38),
        VK_FORMAT_R8G8B8A8_USCALED(39),
        VK_FORMAT_R8G8B8A8_SSCALED(40),
        VK_FORMAT_R8G8B8A8_UINT(41),
        VK_FORMAT_R8G8B8A8_SINT(42),
        VK_FORMAT_R8G8B8A8_SRGB(43),
        VK_FORMAT_B8G8R8A8_UNORM(44),
        VK_FORMAT_B8G8R8A8_SNORM(45),
        VK_FORMAT_B8G8R8A8_USCALED(46),
        VK_FORMAT_B8G8R8A8_SSCALED(47),
        VK_FORMAT_B8G8R8A8_UINT(48),
        VK_FORMAT_B8G8R8A8_SINT(49),
        VK_FORMAT_B8G8R8A8_SRGB(50),
        VK_FORMAT_A8B8G8R8_UNORM_PACK32(51),
        VK_FORMAT_A8B8G8R8_SNORM_PACK32(52),
        VK_FORMAT_A8B8G8R8_USCALED_PACK32(53),
        VK_FORMAT_A8B8G8R8_SSCALED_PACK32(54),
        VK_FORMAT_A8B8G8R8_UINT_PACK32(55),
        VK_FORMAT_A8B8G8R8_SINT_PACK32(56),
        VK_FORMAT_A8B8G8R8_SRGB_PACK32(57),
        VK_FORMAT_A2R10G10B10_UNORM_PACK32(58),
        VK_FORMAT_A2R10G10B10_SNORM_PACK32(59),
        VK_FORMAT_A2R10G10B10_USCALED_PACK32(60),
        VK_FORMAT_A2R10G10B10_SSCALED_PACK32(61),
        VK_FORMAT_A2R10G10B10_UINT_PACK32(62),
        VK_FORMAT_A2R10G10B10_SINT_PACK32(63),
        VK_FORMAT_A2B10G10R10_UNORM_PACK32(64),
        VK_FORMAT_A2B10G10R10_SNORM_PACK325(65),
        VK_FORMAT_A2B10G10R10_USCALED_PACK32(66),
        VK_FORMAT_A2B10G10R10_SSCALED_PACK32(67),
        VK_FORMAT_A2B10G10R10_UINT_PACK32(68),
        VK_FORMAT_A2B10G10R10_SINT_PACK32(69),
        VK_FORMAT_R16_UNORM(70),
        VK_FORMAT_R16_SNORM(71),
        VK_FORMAT_R16_USCALED(72),
        VK_FORMAT_R16_SSCALED(73),
        VK_FORMAT_R16_UINT(74),
        VK_FORMAT_R16_SINT(75),
        VK_FORMAT_R16_SFLOAT(76),
        VK_FORMAT_R16G16_UNORM(77),
        VK_FORMAT_R16G16_SNORM8(78),
        VK_FORMAT_R16G16_USCALED(79),
        VK_FORMAT_R16G16_SSCALED(80),
        VK_FORMAT_R16G16_UINT(81),
        VK_FORMAT_R16G16_SINT(82),
        VK_FORMAT_R16G16_SFLOAT(83),
        VK_FORMAT_R16G16B16_UNORM(84),
        VK_FORMAT_R16G16B16_SNORM(85),
        VK_FORMAT_R16G16B16_USCALED(86),
        VK_FORMAT_R16G16B16_SSCALED(87),
        VK_FORMAT_R16G16B16_UINT(88),
        VK_FORMAT_R16G16B16_SINT(89),
        VK_FORMAT_R16G16B16_SFLOAT(90),
        VK_FORMAT_R16G16B16A16_UNORM(91),
        VK_FORMAT_R16G16B16A16_SNORM(92),
        VK_FORMAT_R16G16B16A16_USCALED(93),
        VK_FORMAT_R16G16B16A16_SSCALED(94),
        VK_FORMAT_R16G16B16A16_UINT(95),
        VK_FORMAT_R16G16B16A16_SINT(96),
        VK_FORMAT_R16G16B16A16_SFLOAT(97),
        VK_FORMAT_R32_UINT(98),
        VK_FORMAT_R32_SINT(99),
        VK_FORMAT_R32_SFLOAT(100),
        VK_FORMAT_R32G32_UINT(101),
        VK_FORMAT_R32G32_SINT(102),
        VK_FORMAT_R32G32_SFLOAT(103),
        VK_FORMAT_R32G32B32_UINT(104),
        VK_FORMAT_R32G32B32_SINT(105),
        VK_FORMAT_R32G32B32_SFLOAT(106),
        VK_FORMAT_R32G32B32A32_UINT(107),
        VK_FORMAT_R32G32B32A32_SINT(108),
        VK_FORMAT_R32G32B32A32_SFLOAT(109),
        VK_FORMAT_R64_UINT(110),
        VK_FORMAT_R64_SINT(111),
        VK_FORMAT_R64_SFLOAT(112),
        VK_FORMAT_R64G64_UINT(113),
        VK_FORMAT_R64G64_SINT(114),
        VK_FORMAT_R64G64_SFLOAT(115),
        VK_FORMAT_R64G64B64_UINT(116),
        VK_FORMAT_R64G64B64_SINT(117),
        VK_FORMAT_R64G64B64_SFLOAT(118),
        VK_FORMAT_R64G64B64A64_UINT(119),
        VK_FORMAT_R64G64B64A64_SINT(120),
        VK_FORMAT_R64G64B64A64_SFLOAT(121),
        VK_FORMAT_B10G11R11_UFLOAT_PACK32(122),
        VK_FORMAT_E5B9G9R9_UFLOAT_PACK32(123),
        VK_FORMAT_D16_UNORM(124),
        VK_FORMAT_X8_D24_UNORM_PACK32(125),
        VK_FORMAT_D32_SFLOAT(126),
        VK_FORMAT_S8_UINT(127),
        VK_FORMAT_D16_UNORM_S8_UINT(128),
        VK_FORMAT_D24_UNORM_S8_UINT(129),
        VK_FORMAT_D32_SFLOAT_S8_UINT(130),
        VK_FORMAT_BC1_RGB_UNORM_BLOCK(131),
        VK_FORMAT_BC1_RGB_SRGB_BLOCK(132),
        VK_FORMAT_BC1_RGBA_UNORM_BLOCK(133),
        VK_FORMAT_BC1_RGBA_SRGB_BLOCK(134),
        VK_FORMAT_BC2_UNORM_BLOCK(135),
        VK_FORMAT_BC2_SRGB_BLOCK(136),
        VK_FORMAT_BC3_UNORM_BLOCK(137),
        VK_FORMAT_BC3_SRGB_BLOCK(138),
        VK_FORMAT_BC4_UNORM_BLOCK(139),
        VK_FORMAT_BC4_SNORM_BLOCK(140),
        VK_FORMAT_BC5_UNORM_BLOCK(141),
        VK_FORMAT_BC5_SNORM_BLOCK(142),
        VK_FORMAT_BC6H_UFLOAT_BLOCK(143),
        VK_FORMAT_BC6H_SFLOAT_BLOCK(144),
        VK_FORMAT_BC7_UNORM_BLOCK(145),
        VK_FORMAT_BC7_SRGB_BLOCK(146),
        VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK(147),
        VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK(148),
        VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK(149),
        VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK(150),
        VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK(151),
        VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK(152),
        VK_FORMAT_EAC_R11_UNORM_BLOCK(153),
        VK_FORMAT_EAC_R11_SNORM_BLOCK(154),
        VK_FORMAT_EAC_R11G11_UNORM_BLOCK(155),
        VK_FORMAT_EAC_R11G11_SNORM_BLOCK(156),
        VK_FORMAT_ASTC_4x4_UNORM_BLOCK(157),
        VK_FORMAT_ASTC_4x4_SRGB_BLOCK(158),
        VK_FORMAT_ASTC_5x4_UNORM_BLOCK(159),
        VK_FORMAT_ASTC_5x4_SRGB_BLOCK(160),
        VK_FORMAT_ASTC_5x5_UNORM_BLOCK(161),
        VK_FORMAT_ASTC_5x5_SRGB_BLOCK(162),
        VK_FORMAT_ASTC_6x5_UNORM_BLOCK(163),
        VK_FORMAT_ASTC_6x5_SRGB_BLOCK(164),
        VK_FORMAT_ASTC_6x6_UNORM_BLOCK(165),
        VK_FORMAT_ASTC_6x6_SRGB_BLOCK(166),
        VK_FORMAT_ASTC_8x5_UNORM_BLOCK(167),
        VK_FORMAT_ASTC_8x5_SRGB_BLOCK(168),
        VK_FORMAT_ASTC_8x6_UNORM_BLOCK(169),
        VK_FORMAT_ASTC_8x6_SRGB_BLOCK(170),
        VK_FORMAT_ASTC_8x8_UNORM_BLOCK(171),
        VK_FORMAT_ASTC_8x8_SRGB_BLOCK(172),
        VK_FORMAT_ASTC_10x5_UNORM_BLOCK(173),
        VK_FORMAT_ASTC_10x5_SRGB_BLOCK(174),
        VK_FORMAT_ASTC_10x6_UNORM_BLOCK(175),
        VK_FORMAT_ASTC_10x6_SRGB_BLOCK(176),
        VK_FORMAT_ASTC_10x8_UNORM_BLOCK(177),
        VK_FORMAT_ASTC_10x8_SRGB_BLOCK(178),
        VK_FORMAT_ASTC_10x10_UNORM_BLOCK(179),
        VK_FORMAT_ASTC_10x10_SRGB_BLOCK(180),
        VK_FORMAT_ASTC_12x10_UNORM_BLOCK(181),
        VK_FORMAT_ASTC_12x10_SRGB_BLOCK(182),
        VK_FORMAT_ASTC_12x12_UNORM_BLOCK(183),
        VK_FORMAT_ASTC_12x12_SRGB_BLOCK(184);
        public final int value;

        private Format(int value) {
            this.value = value;
        }

        public static Format get(int value) {
            for (Format sf : values()) {
                if (sf.value == value) {
                    return sf;
                }
            }
            return null;
        }

    }

    public enum ColorSpaceKHR {
        VK_COLOR_SPACE_SRGB_NONLINEAR_KHR(0),
        VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT(1000104001),
        VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT(1000104002),
        VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT(1000104003),
        VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT(1000104004),
        VK_COLOR_SPACE_BT709_LINEAR_EXT(1000104005),
        VK_COLOR_SPACE_BT709_NONLINEAR_EXT(1000104006),
        VK_COLOR_SPACE_BT2020_LINEAR_EXT(1000104007),
        VK_COLOR_SPACE_HDR10_ST2084_EXT(1000104008),
        VK_COLOR_SPACE_DOLBYVISION_EXT(1000104009),
        VK_COLOR_SPACE_HDR10_HLG_EXT(1000104010),
        VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT(1000104011),
        VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT(1000104012),
        VK_COLOR_SPACE_PASS_THROUGH_EXT(1000104013),
        VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT(1000104014),
        VK_COLOR_SPACE_DISPLAY_NATIVE_AMD(1000213000),
        VK_COLORSPACE_SRGB_NONLINEAR_KHR(VK_COLOR_SPACE_SRGB_NONLINEAR_KHR.value),
        VK_COLOR_SPACE_DCI_P3_LINEAR_EXT(VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT.value);
        public final int value;

        private ColorSpaceKHR(int value) {
            this.value = value;
        }

        public static ColorSpaceKHR get(int value) {
            for (ColorSpaceKHR space : values()) {
                if (value == space.value) {
                    return space;
                }
            }
            return null;
        }

    };

    public enum PrimitiveTopology {
        VK_PRIMITIVE_TOPOLOGY_POINT_LIST(0),
        VK_PRIMITIVE_TOPOLOGY_LINE_LIST(1),
        VK_PRIMITIVE_TOPOLOGY_LINE_STRIP(2),
        VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST(3),
        VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP(4),
        VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN(5),
        VK_PRIMITIVE_TOPOLOGY_LINE_LIST_WITH_ADJACENCY(6),
        VK_PRIMITIVE_TOPOLOGY_LINE_STRIP_WITH_ADJACENCY(7),
        VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST_WITH_ADJACENCY(8),
        VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP_WITH_ADJACENCY(9),
        VK_PRIMITIVE_TOPOLOGY_PATCH_LIST(10),
        VK_PRIMITIVE_TOPOLOGY_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private PrimitiveTopology(int value) {
            this.value = value;
        }

    };

    public enum PipelineCreateFlagBits {
        VK_PIPELINE_CREATE_DISABLE_OPTIMIZATION_BIT(0x00000001),
        VK_PIPELINE_CREATE_ALLOW_DERIVATIVES_BIT(0x00000002),
        VK_PIPELINE_CREATE_DERIVATIVE_BIT(0x00000004),
        VK_PIPELINE_CREATE_VIEW_INDEX_FROM_DEVICE_INDEX_BIT(0x00000008),
        VK_PIPELINE_CREATE_DISPATCH_BASE_BIT(0x00000010),
        VK_PIPELINE_CREATE_DEFER_COMPILE_BIT_NV(0x00000020),
        VK_PIPELINE_CREATE_CAPTURE_STATISTICS_BIT_KHR(0x00000040),
        VK_PIPELINE_CREATE_CAPTURE_INTERNAL_REPRESENTATIONS_BIT_KHR(0x00000080),
        VK_PIPELINE_CREATE_DISPATCH_BASE(VK_PIPELINE_CREATE_DISPATCH_BASE_BIT.value),
        VK_PIPELINE_CREATE_VIEW_INDEX_FROM_DEVICE_INDEX_BIT_KHR(
                VK_PIPELINE_CREATE_VIEW_INDEX_FROM_DEVICE_INDEX_BIT.value),
        VK_PIPELINE_CREATE_DISPATCH_BASE_KHR(VK_PIPELINE_CREATE_DISPATCH_BASE.value),
        VK_PIPELINE_CREATE_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private PipelineCreateFlagBits(int value) {
            this.value = value;
        }

        public static int getMask(PipelineCreateFlagBits[] bits) {
            int mask = 0;
            for (PipelineCreateFlagBits flag : bits) {
                mask += flag.value;
            }
            return mask;
        }
    }

    public enum PolygonMode {
        VK_POLYGON_MODE_FILL(0),
        VK_POLYGON_MODE_LINE(1),
        VK_POLYGON_MODE_POINT(2),
        VK_POLYGON_MODE_FILL_RECTANGLE_NV(1000153000),
        VK_POLYGON_MODE_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private PolygonMode(int value) {
            this.value = value;
        }
    }

    public enum CullModeFlagBits {
        VK_CULL_MODE_NONE(0),
        VK_CULL_MODE_FRONT_BIT(0x00000001),
        VK_CULL_MODE_BACK_BIT(0x00000002),
        VK_CULL_MODE_FRONT_AND_BACK(0x00000003),
        VK_CULL_MODE_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private CullModeFlagBits(int value) {
            this.value = value;
        }
    }

    public enum FrontFace {
        VK_FRONT_FACE_COUNTER_CLOCKWISE(0),
        VK_FRONT_FACE_CLOCKWISE(1),
        VK_FRONT_FACE_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private FrontFace(int value) {
            this.value = value;
        }
    }

    public enum SampleCountFlagBits {
        VK_SAMPLE_COUNT_1_BIT(0x00000001),
        VK_SAMPLE_COUNT_2_BIT(0x00000002),
        VK_SAMPLE_COUNT_4_BIT(0x00000004),
        VK_SAMPLE_COUNT_8_BIT(0x00000008),
        VK_SAMPLE_COUNT_16_BIT(0x00000010),
        VK_SAMPLE_COUNT_32_BIT(0x00000020),
        VK_SAMPLE_COUNT_64_BIT(0x00000040),
        VK_SAMPLE_COUNT_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private SampleCountFlagBits(int value) {
            this.value = value;
        }
    }

    public enum CompareOp {
        VK_COMPARE_OP_NEVER(0),
        VK_COMPARE_OP_LESS(1),
        VK_COMPARE_OP_EQUAL(2),
        VK_COMPARE_OP_LESS_OR_EQUAL(3),
        VK_COMPARE_OP_GREATER(4),
        VK_COMPARE_OP_NOT_EQUAL(5),
        VK_COMPARE_OP_GREATER_OR_EQUAL(6),
        VK_COMPARE_OP_ALWAYS(7),
        VK_COMPARE_OP_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private CompareOp(int value) {
            this.value = value;
        }
    }

    public enum LogicOp {
        VK_LOGIC_OP_CLEAR(0),
        VK_LOGIC_OP_AND(1),
        VK_LOGIC_OP_AND_REVERSE(2),
        VK_LOGIC_OP_COPY(3),
        VK_LOGIC_OP_AND_INVERTED(4),
        VK_LOGIC_OP_NO_OP(5),
        VK_LOGIC_OP_XOR(6),
        VK_LOGIC_OP_OR(7),
        VK_LOGIC_OP_NOR(8),
        VK_LOGIC_OP_EQUIVALENT(9),
        VK_LOGIC_OP_INVERT(10),
        VK_LOGIC_OP_OR_REVERSE(11),
        VK_LOGIC_OP_COPY_INVERTED(12),
        VK_LOGIC_OP_OR_INVERTED(13),
        VK_LOGIC_OP_NAND(14),
        VK_LOGIC_OP_SET(15),
        VK_LOGIC_OP_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private LogicOp(int value) {
            this.value = value;
        }
    }

    public enum BlendOp {
        VK_BLEND_OP_ADD(0),
        VK_BLEND_OP_SUBTRACT(1),
        VK_BLEND_OP_REVERSE_SUBTRACT(2),
        VK_BLEND_OP_MIN(3),
        VK_BLEND_OP_MAX(4),
        VK_BLEND_OP_ZERO_EXT(1000148000),
        VK_BLEND_OP_SRC_EXT(1000148001),
        VK_BLEND_OP_DST_EXT(1000148002),
        VK_BLEND_OP_SRC_OVER_EXT(1000148003),
        VK_BLEND_OP_DST_OVER_EXT(1000148004),
        VK_BLEND_OP_SRC_IN_EXT(1000148005),
        VK_BLEND_OP_DST_IN_EXT(1000148006),
        VK_BLEND_OP_SRC_OUT_EXT(1000148007),
        VK_BLEND_OP_DST_OUT_EXT(1000148008),
        VK_BLEND_OP_SRC_ATOP_EXT(1000148009),
        VK_BLEND_OP_DST_ATOP_EXT(1000148010),
        VK_BLEND_OP_XOR_EXT(1000148011),
        VK_BLEND_OP_MULTIPLY_EXT(1000148012),
        VK_BLEND_OP_SCREEN_EXT(1000148013),
        VK_BLEND_OP_OVERLAY_EXT(1000148014),
        VK_BLEND_OP_DARKEN_EXT(1000148015),
        VK_BLEND_OP_LIGHTEN_EXT(1000148016),
        VK_BLEND_OP_COLORDODGE_EXT(1000148017),
        VK_BLEND_OP_COLORBURN_EXT(1000148018),
        VK_BLEND_OP_HARDLIGHT_EXT(1000148019),
        VK_BLEND_OP_SOFTLIGHT_EXT(1000148020),
        VK_BLEND_OP_DIFFERENCE_EXT(1000148021),
        VK_BLEND_OP_EXCLUSION_EXT(1000148022),
        VK_BLEND_OP_INVERT_EXT(1000148023),
        VK_BLEND_OP_INVERT_RGB_EXT(1000148024),
        VK_BLEND_OP_LINEARDODGE_EXT(1000148025),
        VK_BLEND_OP_LINEARBURN_EXT(1000148026),
        VK_BLEND_OP_VIVIDLIGHT_EXT(1000148027),
        VK_BLEND_OP_LINEARLIGHT_EXT(1000148028),
        VK_BLEND_OP_PINLIGHT_EXT(1000148029),
        VK_BLEND_OP_HARDMIX_EXT(1000148030),
        VK_BLEND_OP_HSL_HUE_EXT(1000148031),
        VK_BLEND_OP_HSL_SATURATION_EXT(1000148032),
        VK_BLEND_OP_HSL_COLOR_EXT(1000148033),
        VK_BLEND_OP_HSL_LUMINOSITY_EXT(1000148034),
        VK_BLEND_OP_PLUS_EXT(1000148035),
        VK_BLEND_OP_PLUS_CLAMPED_EXT(1000148036),
        VK_BLEND_OP_PLUS_CLAMPED_ALPHA_EXT(1000148037),
        VK_BLEND_OP_PLUS_DARKER_EXT(1000148038),
        VK_BLEND_OP_MINUS_EXT(1000148039),
        VK_BLEND_OP_MINUS_CLAMPED_EXT(1000148040),
        VK_BLEND_OP_CONTRAST_EXT(1000148041),
        VK_BLEND_OP_INVERT_OVG_EXT(1000148042),
        VK_BLEND_OP_RED_EXT(1000148043),
        VK_BLEND_OP_GREEN_EXT(1000148044),
        VK_BLEND_OP_BLUE_EXT(1000148045),
        VK_BLEND_OP_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private BlendOp(int value) {
            this.value = value;
        }
    }

    public enum BlendFactor {
        VK_BLEND_FACTOR_ZERO(0),
        VK_BLEND_FACTOR_ONE(1),
        VK_BLEND_FACTOR_SRC_COLOR(2),
        VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR(3),
        VK_BLEND_FACTOR_DST_COLOR(4),
        VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR(5),
        VK_BLEND_FACTOR_SRC_ALPHA(6),
        VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA(7),
        VK_BLEND_FACTOR_DST_ALPHA(8),
        VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA(9),
        VK_BLEND_FACTOR_CONSTANT_COLOR(10),
        VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_COLOR(11),
        VK_BLEND_FACTOR_CONSTANT_ALPHA(12),
        VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_ALPHA(13),
        VK_BLEND_FACTOR_SRC_ALPHA_SATURATE(14),
        VK_BLEND_FACTOR_SRC1_COLOR(15),
        VK_BLEND_FACTOR_ONE_MINUS_SRC1_COLOR(16),
        VK_BLEND_FACTOR_SRC1_ALPHA(17),
        VK_BLEND_FACTOR_ONE_MINUS_SRC1_ALPHA(18),
        VK_BLEND_FACTOR_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private BlendFactor(int value) {
            this.value = value;
        }
    }

    public enum ColorComponentFlagBits {
        VK_COLOR_COMPONENT_R_BIT(0x00000001),
        VK_COLOR_COMPONENT_G_BIT(0x00000002),
        VK_COLOR_COMPONENT_B_BIT(0x00000004),
        VK_COLOR_COMPONENT_A_BIT(0x00000008),
        VK_COLOR_COMPONENT_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private ColorComponentFlagBits(int value) {
            this.value = value;
        }
    }

    public enum AttachmentLoadOp {
        VK_ATTACHMENT_LOAD_OP_LOAD(0),
        VK_ATTACHMENT_LOAD_OP_CLEAR(1),
        VK_ATTACHMENT_LOAD_OP_DONT_CARE(2),
        VK_ATTACHMENT_LOAD_OP_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private AttachmentLoadOp(int value) {
            this.value = value;
        }
    }

    public enum AttachmentStoreOp {
        VK_ATTACHMENT_STORE_OP_STORE(0),
        VK_ATTACHMENT_STORE_OP_DONT_CARE(1),
        VK_ATTACHMENT_STORE_OP_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private AttachmentStoreOp(int value) {
            this.value = value;
        }
    }

    public enum ImageLayout {
        VK_IMAGE_LAYOUT_UNDEFINED(0),
        VK_IMAGE_LAYOUT_GENERAL(1),
        VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL(2),
        VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL(3),
        VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL(4),
        VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL(5),
        VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL(6),
        VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL(7),
        VK_IMAGE_LAYOUT_PREINITIALIZED(8),
        VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_STENCIL_ATTACHMENT_OPTIMAL(1000117000),
        VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_STENCIL_READ_ONLY_OPTIMAL(1000117001),
        VK_IMAGE_LAYOUT_PRESENT_SRC_KHR(1000001002),
        VK_IMAGE_LAYOUT_SHARED_PRESENT_KHR(1000111000),
        VK_IMAGE_LAYOUT_SHADING_RATE_OPTIMAL_NV(1000164003),
        VK_IMAGE_LAYOUT_FRAGMENT_DENSITY_MAP_OPTIMAL_EXT(1000218000),
        VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL_KHR(1000241000),
        VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_OPTIMAL_KHR(1000241001),
        VK_IMAGE_LAYOUT_STENCIL_ATTACHMENT_OPTIMAL_KHR(1000241002),
        VK_IMAGE_LAYOUT_STENCIL_READ_ONLY_OPTIMAL_KHR(1000241003),
        VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_STENCIL_ATTACHMENT_OPTIMAL_KHR(
                VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_STENCIL_ATTACHMENT_OPTIMAL.value),
        VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_STENCIL_READ_ONLY_OPTIMAL_KHR(
                VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_STENCIL_READ_ONLY_OPTIMAL.value),
        VK_IMAGE_LAYOUT_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private ImageLayout(int value) {
            this.value = value;
        }
    }

    public enum SubpassDescriptionFlagBits {
        VK_SUBPASS_DESCRIPTION_PER_VIEW_ATTRIBUTES_BIT_NVX(0x00000001),
        VK_SUBPASS_DESCRIPTION_PER_VIEW_POSITION_X_ONLY_BIT_NVX(0x00000002),
        VK_SUBPASS_DESCRIPTION_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private SubpassDescriptionFlagBits(int value) {
            this.value = value;
        }
    }

    public enum PipelineBindPoint {
        VK_PIPELINE_BIND_POINT_GRAPHICS(0),
        VK_PIPELINE_BIND_POINT_COMPUTE(1),
        VK_PIPELINE_BIND_POINT_RAY_TRACING_NV(1000165000),
        VK_PIPELINE_BIND_POINT_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private PipelineBindPoint(int value) {
            this.value = value;
        }

    }

    public enum PipelineStageFlagBits {
        VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT(0x00000001),
        VK_PIPELINE_STAGE_DRAW_INDIRECT_BIT(0x00000002),
        VK_PIPELINE_STAGE_VERTEX_INPUT_BIT(0x00000004),
        VK_PIPELINE_STAGE_VERTEX_SHADER_BIT(0x00000008),
        VK_PIPELINE_STAGE_TESSELLATION_CONTROL_SHADER_BIT(0x00000010),
        VK_PIPELINE_STAGE_TESSELLATION_EVALUATION_SHADER_BIT(0x00000020),
        VK_PIPELINE_STAGE_GEOMETRY_SHADER_BIT(0x00000040),
        VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT(0x00000080),
        VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT(0x00000100),
        VK_PIPELINE_STAGE_LATE_FRAGMENT_TESTS_BIT(0x00000200),
        VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT(0x00000400),
        VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT(0x00000800),
        VK_PIPELINE_STAGE_TRANSFER_BIT(0x00001000),
        VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT(0x00002000),
        VK_PIPELINE_STAGE_HOST_BIT(0x00004000),
        VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT(0x00008000),
        VK_PIPELINE_STAGE_ALL_COMMANDS_BIT(0x00010000),
        VK_PIPELINE_STAGE_TRANSFORM_FEEDBACK_BIT_EXT(0x01000000),
        VK_PIPELINE_STAGE_CONDITIONAL_RENDERING_BIT_EXT(0x00040000),
        VK_PIPELINE_STAGE_COMMAND_PROCESS_BIT_NVX(0x00020000),
        VK_PIPELINE_STAGE_SHADING_RATE_IMAGE_BIT_NV(0x00400000),
        VK_PIPELINE_STAGE_RAY_TRACING_SHADER_BIT_NV(0x00200000),
        VK_PIPELINE_STAGE_ACCELERATION_STRUCTURE_BUILD_BIT_NV(0x02000000),
        VK_PIPELINE_STAGE_TASK_SHADER_BIT_NV(0x00080000),
        VK_PIPELINE_STAGE_MESH_SHADER_BIT_NV(0x00100000),
        VK_PIPELINE_STAGE_FRAGMENT_DENSITY_PROCESS_BIT_EXT(0x00800000),
        VK_PIPELINE_STAGE_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private PipelineStageFlagBits(int value) {
            this.value = value;
        }
    }

    public enum AccessFlagBits {
        VK_ACCESS_INDIRECT_COMMAND_READ_BIT(0x00000001),
        VK_ACCESS_INDEX_READ_BIT(0x00000002),
        VK_ACCESS_VERTEX_ATTRIBUTE_READ_BIT(0x00000004),
        VK_ACCESS_UNIFORM_READ_BIT(0x00000008),
        VK_ACCESS_INPUT_ATTACHMENT_READ_BIT(0x00000010),
        VK_ACCESS_SHADER_READ_BIT(0x00000020),
        VK_ACCESS_SHADER_WRITE_BIT(0x00000040),
        VK_ACCESS_COLOR_ATTACHMENT_READ_BIT(0x00000080),
        VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT(0x00000100),
        VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT(0x00000200),
        VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT(0x00000400),
        VK_ACCESS_TRANSFER_READ_BIT(0x00000800),
        VK_ACCESS_TRANSFER_WRITE_BIT(0x00001000),
        VK_ACCESS_HOST_READ_BIT(0x00002000),
        VK_ACCESS_HOST_WRITE_BIT(0x00004000),
        VK_ACCESS_MEMORY_READ_BIT(0x00008000),
        VK_ACCESS_MEMORY_WRITE_BIT(0x00010000),
        VK_ACCESS_TRANSFORM_FEEDBACK_WRITE_BIT_EXT(0x02000000),
        VK_ACCESS_TRANSFORM_FEEDBACK_COUNTER_READ_BIT_EXT(0x04000000),
        VK_ACCESS_TRANSFORM_FEEDBACK_COUNTER_WRITE_BIT_EXT(0x08000000),
        VK_ACCESS_CONDITIONAL_RENDERING_READ_BIT_EXT(0x00100000),
        VK_ACCESS_COMMAND_PROCESS_READ_BIT_NVX(0x00020000),
        VK_ACCESS_COMMAND_PROCESS_WRITE_BIT_NVX(0x00040000),
        VK_ACCESS_COLOR_ATTACHMENT_READ_NONCOHERENT_BIT_EXT(0x00080000),
        VK_ACCESS_SHADING_RATE_IMAGE_READ_BIT_NV(0x00800000),
        VK_ACCESS_ACCELERATION_STRUCTURE_READ_BIT_NV(0x00200000),
        VK_ACCESS_ACCELERATION_STRUCTURE_WRITE_BIT_NV(0x00400000),
        VK_ACCESS_FRAGMENT_DENSITY_MAP_READ_BIT_EXT(0x01000000),
        VK_ACCESS_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private AccessFlagBits(int value) {
            this.value = value;
        }
    }

    public enum DependencyFlagBits {
        VK_DEPENDENCY_BY_REGION_BIT(0x00000001),
        VK_DEPENDENCY_DEVICE_GROUP_BIT(0x00000004),
        VK_DEPENDENCY_VIEW_LOCAL_BIT(0x00000002),
        VK_DEPENDENCY_VIEW_LOCAL_BIT_KHR(VK_DEPENDENCY_VIEW_LOCAL_BIT.value),
        VK_DEPENDENCY_DEVICE_GROUP_BIT_KHR(VK_DEPENDENCY_DEVICE_GROUP_BIT.value),
        VK_DEPENDENCY_FLAG_BITS_MAX_ENUM(0x7FFFFFFF);
        public final int value;

        private DependencyFlagBits(int value) {
            this.value = value;
        }
    }

    public enum Extensions {
        VK_KHR_16bit_storage(),
        VK_KHR_8bit_storage(),
        VK_KHR_android_surface(),
        VK_KHR_bind_memory2(),
        VK_KHR_create_renderpass2(),
        VK_KHR_dedicated_allocation(),
        VK_KHR_depth_stencil_resolve(),
        VK_KHR_descriptor_update_template(),
        VK_KHR_device_group(),
        VK_KHR_device_group_creation(),
        VK_KHR_display(),
        VK_KHR_display_swapchain(),
        VK_KHR_draw_indirect_count(),
        VK_KHR_driver_properties(),
        VK_KHR_external_fence(),
        VK_KHR_external_fence_capabilities(),
        VK_KHR_external_fence_fd(),
        VK_KHR_external_fence_win32(),
        VK_KHR_external_memory(),
        VK_KHR_external_memory_capabilities(),
        VK_KHR_external_memory_fd(),
        VK_KHR_external_memory_win32(),
        VK_KHR_external_semaphore(),
        VK_KHR_external_semaphore_capabilities(),
        VK_KHR_external_semaphore_fd(),
        VK_KHR_external_semaphore_win32(),
        VK_KHR_get_display_properties2(),
        VK_KHR_get_memory_requirements2(),
        VK_KHR_get_physical_device_properties2(),
        VK_KHR_get_surface_capabilities2(),
        VK_KHR_image_format_list(),
        VK_KHR_incremental_present(),
        VK_KHR_maintenance1(),
        VK_KHR_maintenance2(),
        VK_KHR_maintenance3(),
        VK_KHR_multiview(),
        VK_KHR_push_descriptor(),
        VK_KHR_relaxed_block_layout(),
        VK_KHR_sampler_mirror_clamp_to_edge(),
        VK_KHR_sampler_ycbcr_conversion(),
        VK_KHR_shader_atomic_int64(),
        VK_KHR_shader_draw_parameters(),
        VK_KHR_shader_float16_int8(),
        VK_KHR_shader_float_controls(),
        VK_KHR_shared_presentable_image(),
        VK_KHR_storage_buffer_storage_class(),
        VK_KHR_surface(),
        VK_KHR_surface_protected_capabilities(),
        VK_KHR_swapchain(),
        VK_KHR_swapchain_mutable_format(),
        VK_KHR_uniform_buffer_standard_layout(),
        VK_KHR_variable_pointers(),
        VK_KHR_vulkan_memory_model(),
        VK_KHR_wayland_surface(),
        VK_KHR_win32_keyed_mutex(),
        VK_KHR_win32_surface(),
        VK_KHR_xcb_surface(),
        VK_KHR_xlib_surface(),
        VK_EXT_acquire_xlib_display(),
        VK_EXT_astc_decode_mode(),
        VK_EXT_blend_operation_advanced(),
        VK_EXT_buffer_device_address(),
        VK_EXT_calibrated_timestamps(),
        VK_EXT_conditional_rendering(),
        VK_EXT_conservative_rasterization(),
        VK_EXT_debug_marker(),
        VK_EXT_debug_report(),
        VK_EXT_debug_utils(),
        VK_EXT_depth_clip_enable(),
        VK_EXT_depth_range_unrestricted(),
        VK_EXT_descriptor_indexing(),
        VK_EXT_direct_mode_display(),
        VK_EXT_discard_rectangles(),
        VK_EXT_display_control(),
        VK_EXT_display_surface_counter(),
        VK_EXT_external_memory_dma_buf(),
        VK_EXT_external_memory_host(),
        VK_EXT_filter_cubic(),
        VK_EXT_fragment_density_map(),
        VK_EXT_fragment_shader_interlock(),
        VK_EXT_full_screen_exclusive(),
        VK_EXT_global_priority(),
        VK_EXT_hdr_metadata(),
        VK_EXT_headless_surface(),
        VK_EXT_host_query_reset(),
        VK_EXT_image_drm_format_modifier(),
        VK_EXT_inline_uniform_block(),
        VK_EXT_memory_budget(),
        VK_EXT_memory_priority(),
        VK_EXT_metal_surface(),
        VK_EXT_pci_bus_info(),
        VK_EXT_pipeline_creation_feedback(),
        VK_EXT_post_depth_coverage(),
        VK_EXT_queue_family_foreign(),
        VK_EXT_sample_locations(),
        VK_EXT_sampler_filter_minmax(),
        VK_EXT_scalar_block_layout(),
        VK_EXT_separate_stencil_usage(),
        VK_EXT_shader_stencil_export(),
        VK_EXT_shader_subgroup_ballot(),
        VK_EXT_shader_subgroup_vote(),
        VK_EXT_shader_viewport_index_layer(),
        VK_EXT_swapchain_colorspace(),
        VK_EXT_transform_feedback(),
        VK_EXT_validation_cache(),
        VK_EXT_validation_features(),
        VK_EXT_validation_flags(),
        VK_EXT_vertex_attribute_divisor(),
        VK_EXT_ycbcr_image_arrays(),
        VK_AMD_buffer_marker(),
        VK_AMD_display_native_hdr(),
        VK_AMD_draw_indirect_count(),
        VK_AMD_gcn_shader(),
        VK_AMD_gpu_shader_half_float(),
        VK_AMD_gpu_shader_int16(),
        VK_AMD_memory_overallocation_behavior(),
        VK_AMD_mixed_attachment_samples(),
        VK_AMD_negative_viewport_height(),
        VK_AMD_rasterization_order(),
        VK_AMD_shader_ballot(),
        VK_AMD_shader_core_properties(),
        VK_AMD_shader_explicit_vertex_parameter(),
        VK_AMD_shader_fragment_mask(),
        VK_AMD_shader_image_load_store_lod(),
        VK_AMD_shader_info(),
        VK_AMD_shader_trinary_minmax(),
        VK_AMD_texture_gather_bias_lod(),
        VK_ANDROID_external_memory_android_hardware_buffer(),
        VK_FUCHSIA_imagepipe_surface(),
        VK_GGP_frame_token(),
        VK_GGP_stream_descriptor_surface(),
        VK_GOOGLE_decorate_string(),
        VK_GOOGLE_display_timing(),
        VK_GOOGLE_hlsl_functionality1(),
        VK_IMG_filter_cubic(),
        VK_IMG_format_pvrtc(),
        VK_INTEL_performance_query(),
        VK_INTEL_shader_integer_functions2(),
        VK_MVK_ios_surface(),
        VK_MVK_macos_surface(),
        VK_NN_vi_surface(),
        VK_NV_clip_space_w_scaling(),
        VK_NV_compute_shader_derivatives(),
        VK_NV_cooperative_matrix(),
        VK_NV_corner_sampled_image(),
        VK_NV_coverage_reduction_mode(),
        VK_NV_dedicated_allocation(),
        VK_NV_dedicated_allocation_image_aliasing(),
        VK_NV_device_diagnostic_checkpoints(),
        VK_NV_external_memory(),
        VK_NV_external_memory_capabilities(),
        VK_NV_external_memory_win32(),
        VK_NV_fill_rectangle(),
        VK_NV_fragment_coverage_to_color(),
        VK_NV_fragment_shader_barycentric(),
        VK_NV_framebuffer_mixed_samples(),
        VK_NV_geometry_shader_passthrough(),
        VK_NV_glsl_shader(),
        VK_NV_mesh_shader(),
        VK_NV_ray_tracing(),
        VK_NV_representative_fragment_test(),
        VK_NV_sample_mask_override_coverage(),
        VK_NV_scissor_exclusive(),
        VK_NV_shader_image_footprint(),
        VK_NV_shader_sm_builtins(),
        VK_NV_shader_subgroup_partitioned(),
        VK_NV_shading_rate_image(),
        VK_NV_viewport_array2(),
        VK_NV_viewport_swizzle(),
        VK_NV_win32_keyed_mutex(),
        VK_NVX_device_generated_commands(),
        VK_NVX_image_view_handle(),
        VK_NVX_multiview_per_view_attributes();
    }
}
