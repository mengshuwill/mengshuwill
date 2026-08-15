package com.zenresearch.medrisk;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final List<RadioGroup> groups = new ArrayList<>();
    private final List<CheckBox> extras = new ArrayList<>();
    private EditText caseId, notes;
    private Spinner procedure;
    private TextView result;

    private final String[] titles = {
        "病情认识过于乐观",
        "对治疗效果期望高",
        "对生活质量要求高、对不舒服耐受差",
        "很有主见，不太容易听进去别人的意见",
        "比较精明、心细，特别在意细节",
        "对医生护士信任度不高",
        "不太好沟通、自说自话或比较固执",
        "比较冷淡，不太搭理医护",
        "对身体不舒服特别敏感",
        "小事容易不高兴、反复纠缠",
        "反复强调钱和经济压力",
        "家属关系复杂、重要家属经常不在场",
        "以前经常觉得医院或医生有问题",
        "术前已经谈责任、赔偿、投诉或曝光",
        "患者本身状态很差",
        "这个手术本身有点勉强（边界适应证）",
        "近期病情不稳定或明显失代偿"
    };

    private final String[] hints = {
        "病情明明较重，但仍觉得“人现在挺好的，应该没什么大问题”。",
        "希望做了就好；觉得微创就应该风险小、恢复快，对疗效预期偏高。",
        "很在意疼痛、发热、乏力、吃饭、活动和恢复速度，对副作用容忍低。",
        "自己已有一套判断，医生解释后仍不太愿意调整或接受其他方案。",
        "很会记细节、比较不同医生说法，对时间、费用、检查、用药特别敏感。",
        "喜欢多问几个人，怀疑是否有事没说清，护士或年轻医生的话不太信。",
        "容易打断、绕回自己的观点，反复解释仍不容易达成一致。",
        "回应少、交流封闭，表面说知道了，但不容易判断是否真正接受。",
        "一点疼、恶心、发热、乏力就很在意，同一不适容易反复找医护。",
        "对等待、床位、检查顺序、回应速度等小问题容易持续不满。",
        "经常说已经花很多钱、费用压力大，容易形成“花这么多就应该有效”的预期。",
        "陪护人、签字人、出钱人、真正做主的人不一致，或关键家属很少出现。",
        "既往看病时常觉得多家医院/医生有问题，或已有投诉、反复换医经历。",
        "出现“出问题谁负责、要赔、要投诉/曝光”等表达；少见但一旦出现要重视。",
        "如 Child-Pugh C、MELD≥18、PS≥3，白细胞/中性粒细胞严重低，肝功能或全身状态差。",
        "能做，但风险和获益接近；医方自己也觉得做起来比较勉强。",
        "近期/本次有消化道出血、肝性脑病、感染、大量腹水、AKI/HRS、明显黄疸等。"
    };

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(127,29,29));
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = col();
        root.setPadding(dp(14), dp(14), dp(14), dp(40));
        scroll.addView(root); setContentView(scroll);

        LinearLayout hero = col();
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(dp(14),dp(18),dp(14),dp(16));
        hero.setBackgroundColor(Color.rgb(255,247,247));
        ImageView logo = new ImageView(this);
        logo.setImageResource(com.zenresearch.medrisk.R.drawable.app_logo);
        hero.addView(logo, new LinearLayout.LayoutParams(dp(92), dp(92)));
        TextView h1 = text("介入术前风险快速识别",23,Color.rgb(127,29,29),true);
        h1.setGravity(Gravity.CENTER); hero.addView(h1, matchWrap());
        TextView h2 = text("先看患方特点，再看医学风险 · V2.1",13,Color.rgb(100,116,139),false);
        h2.setGravity(Gravity.CENTER); hero.addView(h2, matchWrap());
        root.addView(hero, matchWrap());

        addSectionTitle(root,"病例信息（可选）");
        caseId = field(root,"病例编号","建议不用姓名，例如 TIPS-026");
        procedure = new Spinner(this);
        String[] ps={"TIPS","TACE","肿瘤消融","血管栓塞","穿刺/引流","其他"};
        procedure.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,ps));
        root.addView(procedure,matchWrap());

        addSectionTitle(root,"A. 患方风险 · 快速看人（14项）");
        TextView aTip=text("直接凭住院期间接触判断：没有 / 有一点 / 明显。单个特点不代表高风险，看的是多个特点叠加。",13,Color.rgb(100,116,139),false);
        root.addView(aTip,matchWrap());
        for(int i=0;i<14;i++) addItem(root,i);

        addSectionTitle(root,"B. 医学风险（3项）");
        for(int i=14;i<17;i++) addItem(root,i);

        addSectionTitle(root,"医学高危点（可勾选，不重复加分）");
        String[] ex={"Child-Pugh C","MELD ≥18","PS/ECOG ≥3","白细胞明显降低","中性粒细胞明显降低","血小板明显降低","肝功能很差/明显黄疸","肾功能差/AKI/HRS","营养差/全身状态差","消化道出血","肝性脑病","感染","大量/难治性腹水","近期反复失代偿"};
        for(String s:ex){ CheckBox cb=new CheckBox(this); cb.setText(s); cb.setTextSize(14); extras.add(cb); root.addView(cb); }

        notes = field(root,"备注","例如：真正做主的家属是谁、特殊沟通情况等");
        notes.setSingleLine(false); notes.setMinLines(2);

        Button calc=button("计算风险");
        calc.setBackgroundColor(Color.rgb(185,28,28)); calc.setTextColor(Color.WHITE);
        calc.setOnClickListener(v->calculate()); root.addView(calc,matchWrap());

        result=text("尚未评估",17,Color.WHITE,true);
        result.setPadding(dp(16),dp(16),dp(16),dp(16));
        result.setBackgroundColor(Color.rgb(100,116,139));
        LinearLayout.LayoutParams rp=matchWrap(); rp.setMargins(0,dp(12),0,dp(10)); root.addView(result,rp);

        Button copy=button("复制结果"); copy.setOnClickListener(v->copyResult()); root.addView(copy,matchWrap());
        Button reset=button("重新评估"); reset.setOnClickListener(v->reset()); root.addView(reset,matchWrap());

        TextView foot=text("仅用于科室术前风险提醒和沟通管理，不是经过验证的纠纷预测模型，也不用于拒绝患者治疗。",11,Color.rgb(100,116,139),false);
        LinearLayout.LayoutParams fp=matchWrap(); fp.setMargins(0,dp(12),0,0); root.addView(foot,fp);
    }

    private void addItem(LinearLayout root,int i){
        TextView t=text((i+1)+". "+titles[i],16,Color.rgb(30,41,59),true);
        LinearLayout.LayoutParams p=matchWrap(); p.setMargins(0,dp(14),0,dp(3)); root.addView(t,p);
        TextView h=text(hints[i],12,Color.rgb(100,116,139),false); root.addView(h,matchWrap());
        RadioGroup g=new RadioGroup(this); g.setOrientation(RadioGroup.HORIZONTAL);
        String[] labels={"没有","有一点","明显"};
        for(int n=0;n<3;n++){
            RadioButton r=new RadioButton(this); r.setId(View.generateViewId()); r.setTag(n); r.setText(labels[n]); r.setTextSize(14);
            g.addView(r,new RadioGroup.LayoutParams(0,dp(46),1));
            if(n==0) r.setChecked(true);
        }
        groups.add(g); root.addView(g,matchWrap());
    }

    private void calculate(){
        int a=0,b=0; int[] v=new int[17];
        List<String> patientFlags=new ArrayList<>(), medicalFlags=new ArrayList<>();
        for(int i=0;i<groups.size();i++){
            RadioButton r=groups.get(i).findViewById(groups.get(i).getCheckedRadioButtonId());
            v[i]=(Integer)r.getTag();
            if(i<14){ a+=v[i]; if(v[i]==2) patientFlags.add(titles[i]); }
            else { b+=v[i]; if(v[i]==2) medicalFlags.add(titles[i]); }
        }
        int level;
        if(a>=18 || (a>=12 && b>=4)) level=4;
        else if(a>=12 || b>=4 || (a>=6 && b>=2)) level=3;
        else if(a>=6 || b>=2) level=2;
        else level=1;
        if(v[13]==2 && level<3) level=3;
        if(v[15]==2 && (v[14]==2 || v[16]==2) && level<3) level=3;

        String[] names={"","🟢 低风险","🟡 中等风险","🟠 高风险","🔴 重点患者"};
        String[] advice={"","常规术前沟通即可。","主刀/术者再重点讲一次主要风险，确认真正做主的家属已经听明白。","建议上级医师参与；关键家属到场；把最可能出现的不良结果和治疗边界讲透并记录。","建议主任参与；关键家属必须到场；必要时MDT/医务部门提前知情；治疗与不治疗风险、替代方案及最坏结果都要讲清并留痕。"};
        int color=level==1?Color.rgb(22,163,74):level==2?Color.rgb(202,138,4):level==3?Color.rgb(234,88,12):Color.rgb(185,28,28);
        StringBuilder sb=new StringBuilder(names[level]);
        sb.append("\n\n患方：").append(riskA(a)).append("  ").append(a).append("/28");
        sb.append("\n医学：").append(riskB(b)).append("  ").append(b).append("/6");
        if(!patientFlags.isEmpty()) sb.append("\n\n患方明显特点：\n• ").append(String.join("\n• ",patientFlags));
        if(!medicalFlags.isEmpty()) sb.append("\n\n医学明显风险：\n• ").append(String.join("\n• ",medicalFlags));
        List<String> checked=new ArrayList<>(); for(CheckBox c:extras) if(c.isChecked()) checked.add(c.getText().toString());
        if(!checked.isEmpty()) sb.append("\n\n医学高危点：\n• ").append(String.join("\n• ",checked));
        sb.append("\n\n建议：").append(advice[level]);
        result.setText(sb.toString()); result.setBackgroundColor(color); result.setTag(sb.toString());
    }

    private String riskA(int s){ return s<=5?"低":s<=11?"中":s<=17?"高":"很高"; }
    private String riskB(int s){ return s<=1?"低":s<=3?"中":"高"; }

    private void copyResult(){
        if(result.getTag()==null){ toast("请先计算风险"); return; }
        String txt="《介入术前风险快速识别》\n病例编号："+caseId.getText()+"\n介入类型："+procedure.getSelectedItem()+"\n评估日期："+new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date())+"\n\n"+result.getTag()+"\n\n备注："+notes.getText();
        ClipboardManager cm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("术前风险",txt)); toast("结果已复制");
    }

    private void reset(){
        for(RadioGroup g:groups){ ((RadioButton)g.getChildAt(0)).setChecked(true); }
        for(CheckBox c:extras)c.setChecked(false);
        caseId.setText(""); notes.setText(""); procedure.setSelection(0);
        result.setText("尚未评估"); result.setTag(null); result.setBackgroundColor(Color.rgb(100,116,139));
    }

    private LinearLayout col(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private TextView text(String s,int sp,int color,boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(color); if(bold)t.setTypeface(null,1); t.setLineSpacing(0,1.12f); return t; }
    private void addSectionTitle(LinearLayout root,String s){ TextView t=text(s,18,Color.rgb(127,29,29),true); LinearLayout.LayoutParams p=matchWrap(); p.setMargins(0,dp(18),0,dp(8)); root.addView(t,p); }
    private EditText field(LinearLayout root,String label,String hint){ TextView l=text(label,13,Color.rgb(71,85,105),false); LinearLayout.LayoutParams lp=matchWrap(); lp.setMargins(0,dp(8),0,dp(3)); root.addView(l,lp); EditText e=new EditText(this); e.setTextSize(15); e.setHint(hint); root.addView(e,matchWrap()); return e; }
    private Button button(String s){ Button b=new Button(this); b.setText(s); b.setTextSize(15); b.setAllCaps(false); return b; }
    private LinearLayout.LayoutParams matchWrap(){ return new LinearLayout.LayoutParams(-1,-2); }
    private int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
