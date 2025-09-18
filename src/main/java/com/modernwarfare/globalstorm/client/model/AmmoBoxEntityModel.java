package com.modernwarfare.globalstorm.client.model;// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.modernwarfare.globalstorm.GlobalStorm;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class AmmoBoxEntityModel<T extends Entity> extends EntityModel<T> {

	private final ModelPart bone;
	private final ModelPart ammo;

	public AmmoBoxEntityModel(ModelPart root) {
		this.bone = root.getChild("bone");
		this.ammo = root.getChild("ammo");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 42).addBox(-7.0F, -7.0F, -5.0F, 1.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(-6.0F, -1.0F, -4.5F, 12.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(22, 42).addBox(6.0F, -7.0F, -5.0F, 1.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(42, 17).addBox(-5.5F, -7.5F, 5.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 30).addBox(2.5F, -7.5F, 5.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 6).addBox(3.0F, -7.0F, 5.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 14).addBox(-5.0F, -7.0F, 5.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(58, 42).addBox(3.0F, -7.0F, -6.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 22).addBox(-5.0F, -7.0F, -6.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 34).addBox(-7.5F, -7.0F, -5.5F, 15.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 34).addBox(-7.5F, -7.0F, 4.5F, 15.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-5.0F, -0.75F, -6.0F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(28, 21).addBox(3.0F, -0.75F, -6.0F, 2.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.5F, -0.25F, -5.0F, 15.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.125F, -6.75F, 2.0F, 0.0564F, -0.0234F, -1.1788F));

		PartDefinition ammo = partdefinition.addOrReplaceChild("ammo", CubeListBuilder.create().texOffs(42, 11).addBox(-5.5F, -3.0F, 0.25F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(44, 42).addBox(-5.5F, -3.0F, -4.25F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(50, 0).addBox(-2.0F, -3.0F, 0.25F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(58, 50).addBox(3.5F, -4.0F, 1.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(50, 6).addBox(4.0F, -4.75F, 2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(44, 48).addBox(-2.0F, -3.0F, -4.25F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r2 = ammo.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 60).addBox(-1.7333F, -3.7718F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0167F, -3.5218F, 0.0F, 1.4882F, 0.7757F, -0.0418F));

		PartDefinition cube_r3 = ammo.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 60).addBox(-1.7333F, -0.2282F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0167F, -3.5218F, 0.0F, 1.4549F, 1.0362F, -0.0837F));

		PartDefinition cube_r4 = ammo.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 59).addBox(-1.7333F, -3.7718F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9833F, -4.2718F, -2.5F, 0.1687F, -0.045F, 0.258F));

		PartDefinition cube_r5 = ammo.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 59).addBox(-1.7333F, -0.2282F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9833F, -4.2718F, -2.5F, 0.1745F, 0.0F, 0.0F));

		PartDefinition cube_r6 = ammo.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(16, 59).addBox(-1.7333F, -3.7718F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9833F, -4.2718F, -3.625F, 0.1265F, -0.0338F, 0.2597F));

		PartDefinition cube_r7 = ammo.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(8, 59).addBox(-1.7333F, -0.2282F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9833F, -4.2718F, -3.625F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r8 = ammo.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(62, 6).addBox(-0.5F, -2.125F, -1.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 59).addBox(-1.0F, -1.375F, -1.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.875F, 2.75F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r9 = ammo.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(56, 60).addBox(-0.5F, -1.875F, -0.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(58, 55).addBox(-1.0F, -1.125F, -0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -2.875F, -0.25F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r10 = ammo.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(44, 54).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -4.5F, 2.25F, -0.3043F, -0.0262F, -0.2141F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void setupAnim(float p_103812_, float p_103813_) {
		this.ammo.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.ammo.xRot = p_103813_ * ((float)Math.PI / 180F);
		this.bone.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.bone.xRot = p_103813_ * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		ammo.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

}