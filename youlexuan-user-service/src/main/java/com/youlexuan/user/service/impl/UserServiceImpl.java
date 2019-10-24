package com.youlexuan.user.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youlexuan.mapper.TbUserMapper;
import com.youlexuan.pojo.TbUser;
import com.youlexuan.pojo.TbUserExample;
import com.youlexuan.pojo.TbUserExample.Criteria;
import com.youlexuan.user.service.UserService;

import com.youlexuan.entity.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;


/**
 * 用户表服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {



    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsSendDestination;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
	private TbUserMapper userMapper;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;

    /**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
	    user.setCreated(new Date());
	    user.setUpdated(new Date());
        String newPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(newPassword);
		userMapper.insert(user);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void createSmsCode(String phone) {
        //生成6位随机数
        String code =   (Math.random()*1000000)+"";
        System.out.println("验证码："+code);
        //存入缓存
        redisTemplate.boundHashOps("smscode").put(phone, code);
        redisTemplate.boundHashOps("smscode").expire(5, TimeUnit.MINUTES);
        //发送到activeMQ	....
        jmsTemplate.send(this.smsSendDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Map map = new HashMap<>();
                map.put("mobile",phone);
                map.put("template_code",template_code);
                map.put("sign_name",sign_name);
                Map param = new HashMap<>();
                map.put("param",param);
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",phone);
                mapMessage.setString("template_code",template_code);
                mapMessage.setString("sign_name",sign_name);
                mapMessage.setObject("param",param);

                return mapMessage;
            }
        });
    }

    /**
     * 判断验证码是否正确
     */
    public boolean  checkSmsCode(String phone,String code){
        //得到缓存中存储的验证码
        String sysCode = (String) redisTemplate.boundHashOps("smscode").get(phone);
        if(sysCode==null){
            return false;
        }
        if(!sysCode.equals(code)){
            return false;
        }
        return true;
    }

}
